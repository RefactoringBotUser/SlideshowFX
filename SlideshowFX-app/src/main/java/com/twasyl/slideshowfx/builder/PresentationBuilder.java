/*
 * Copyright 2014 Thierry Wasylczenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twasyl.slideshowfx.builder;

import com.twasyl.slideshowfx.builder.template.DynamicAttribute;
import com.twasyl.slideshowfx.builder.template.SlideTemplate;
import com.twasyl.slideshowfx.builder.template.Template;
import com.twasyl.slideshowfx.exceptions.InvalidPresentationConfigurationException;
import com.twasyl.slideshowfx.exceptions.InvalidTemplateConfigurationException;
import com.twasyl.slideshowfx.exceptions.InvalidTemplateException;
import com.twasyl.slideshowfx.exceptions.PresentationException;
import com.twasyl.slideshowfx.utils.DOMUtils;
import com.twasyl.slideshowfx.utils.ZipUtils;
import javafx.embed.swing.SwingFXUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.json.*;
import javax.json.stream.JsonGenerator;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PresentationBuilder {

    private static final Logger LOGGER = Logger.getLogger(PresentationBuilder.class.getName());
    private static final String VELOCITY_SLIDE_NUMBER_TOKEN = "slideNumber";
    private static final String VELOCITY_SFX_CALLBACK_TOKEN = "sfxCallback";
    private static final String VELOCITY_SFX_CONTENT_DEFINER_TOKEN = "sfxContentDefiner";
    private static final String VELOCITY_SLIDE_ID_PREFIX_TOKEN = "slideIdPrefix";

    private static final String VELOCITY_SFX_CONTENT_DEFINER_SCRIPT = "function setField(slide, what, value) {\n" +
            "\telement = document.getElementById(slide + \"-\" + what);\n" +
            "\telement.innerHTML = decodeURIComponent(escape(window.atob(value)));\n" +
            "}";
    private static final String VELOCITY_SFX_CALLBACK_SCRIPT = "function sendInformationToSlideshowFX(source) {\n" +
            "\tdashIndex = source.id.indexOf(\"-\");\n" +
            "\tslideNumber = source.id.substring(0, dashIndex);\n" +
            "\tfieldName = source.id.substring(dashIndex+1);\n" +
            "\n" +
            "\tsfx.prefillContentDefinition(slideNumber, fieldName, window.btoa(unescape(encodeURIComponent(source.innerHTML))));\n" +
            "}";

    private static final String VELOCITY_SFX_CALLBACK_CALL = "sendInformationToSlideshowFX(this);";

    private Template template;
    private Presentation presentation;
    private File templateArchiveFile;
    private File presentationArchiveFile;

    public PresentationBuilder() {
    }

    public PresentationBuilder(File template) {
        this.templateArchiveFile = template;
    }

    public File getTemplateArchiveFile() { return this.templateArchiveFile; }
    public void setTemplateArchiveFile(File template) { this.templateArchiveFile = template; }

    public File getPresentationArchiveFile() { return presentationArchiveFile; }
    public void setPresentationArchiveFile(File presentationArchiveFile) { this.presentationArchiveFile = presentationArchiveFile; }

    public Presentation getPresentation() { return this.presentation; }
    public void setPresentation(Presentation presentationFile) { this.presentation = presentationFile; }

    public Template getTemplate() { return template; }
    public void setTemplate(Template template) { this.template = template; }

    /**
     * Prepare the resources:
     * <ul>
     *     <li>Create an instance of Template</li>
     *     <li>Create the temporary folder</li>
     *     <li>Extract the data</li>
     *     <li>Load the template data</li>
     * </ul>
     */
    private void prepareResources(File dataArchive) throws InvalidTemplateException, InvalidTemplateConfigurationException {
        if(dataArchive == null) throw new IllegalArgumentException("Can not prepare the resources: the dataArchive is null");
        if(!dataArchive.exists()) throw new IllegalArgumentException("Can not prepare the resources: dataArchive does not exist");

        this.template = new Template();
        this.template.setContentDefinerMethod("setField");
        this.template.setFolder(new File(System.getProperty("java.io.tmpdir") + File.separator + "sfx-" + System.currentTimeMillis()));

        LOGGER.fine("The temporaryTemplateFolder is " + this.template.getFolder().getAbsolutePath());

        this.template.getFolder().deleteOnExit();

        // Unzip the template into a temporary folder
        LOGGER.fine("Extracting the template ...");

        try {
            ZipUtils.unzip(dataArchive, this.template.getFolder());
        } catch (IOException e) {
            throw new InvalidTemplateException("Error while trying to unzip the template", e);
        }

        // Read the configuration
        try {
            this.template.readFromFolder();
        } catch (FileNotFoundException e) {
            throw new InvalidTemplateConfigurationException("Can not read template's configuration");
        }
    }

    /**
     * Load the current template defined by the templateArchiveFile attribute.
     * This creates a temporary file.
     */
    public void loadTemplate() throws InvalidTemplateException, InvalidTemplateConfigurationException, PresentationException {
        this.prepareResources(this.templateArchiveFile);

        // Copy the template to the presentation file
        LOGGER.fine("Creating presentation file");
        this.presentation = new Presentation();
        this.presentation.setSlides(new ArrayList<Slide>());
        this.presentation.setPresentationFile(new File(this.template.getFolder(), Presentation.PRESENTATION_FILE_NAME));

        // Replacing the velocity tokens
        try(final InputStream inputStream = new FileInputStream(this.template.getFile());
            final Reader reader = new InputStreamReader(inputStream);
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final Writer writer = new OutputStreamWriter(outputStream)) {

            Velocity.init();
            VelocityContext context = new VelocityContext();
            context.put(VELOCITY_SFX_CONTENT_DEFINER_TOKEN, VELOCITY_SFX_CONTENT_DEFINER_SCRIPT);
            context.put(VELOCITY_SFX_CALLBACK_TOKEN, VELOCITY_SFX_CALLBACK_SCRIPT);

            Velocity.evaluate(context, writer, "", reader);

            writer.flush();
            outputStream.flush();

            this.presentation.setDocument(Jsoup.parse(outputStream.toString("UTF8")));

            this.saveTemporaryPresentation();

        } catch (FileNotFoundException e) {
            throw new InvalidTemplateException("The template file is not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the given template
     * @param template
     * @throws IOException
     */
    public void loadTemplate(File template) throws InvalidTemplateException, InvalidTemplateConfigurationException, PresentationException {
        setTemplateArchiveFile(template);
        this.loadTemplate();
    }

    /**
     * Open a saved presentation
     */
    public void openPresentation() throws InvalidTemplateException, InvalidTemplateConfigurationException, InvalidPresentationConfigurationException, PresentationException {
        this.prepareResources(this.templateArchiveFile);
        this.presentationArchiveFile = this.templateArchiveFile;

        // Copy the template to the presentation file
        LOGGER.fine("Creating presentation file");
        this.presentation = new Presentation();
        this.presentation.setSlides(new ArrayList<>());
        this.presentation.setPresentationFile(new File(this.template.getFolder(), Presentation.PRESENTATION_FILE_NAME));

        // Reading the slides' configuration
        LOGGER.fine("Parsing presentation configuration");

        JsonReader reader = null;
        try {
            reader = Json.createReader(new FileInputStream(new File(this.template.getFolder(), Presentation.PRESENTATION_CONFIGURATION_NAME)));
        } catch (FileNotFoundException e) {
            throw new InvalidPresentationConfigurationException("Can not read presentation configuration file", e);
        }
        JsonObject presentationJson = reader.readObject().getJsonObject("presentation");
        JsonArray slidesJson = presentationJson.getJsonArray("slides");
        JsonObject slideJson;
        JsonArray slideElementsJson;
        Slide slide;
        SlideElement slideElement;

        LOGGER.fine("Reading slides configuration");
        for(int index = 0; index < slidesJson.size(); index++)  {
            slide = new Slide();

            slideJson = slidesJson.getJsonObject(index);
            slide.setId(slideJson.getString("id"));
            slide.setSlideNumber(slideJson.getString("number"));
            slide.setTemplate(this.template.getSlideTemplate(slideJson.getInt("template-id")));

            try {
                slide.setThumbnail(SwingFXUtils.toFXImage(ImageIO.read(new File(this.template.getSlidesThumbnailDirectory(), slide.getSlideNumber().concat(".png"))), null));
            } catch (IOException e) {
                e.printStackTrace();
            }

            slideElementsJson = slideJson.getJsonArray("elements");

            if(slideElementsJson != null && !slideElementsJson.isEmpty()) {
                for(JsonObject slideElementJson : slideElementsJson.getValuesAs(JsonObject.class)) {
                    slideElement = new SlideElement();
                    slideElement.setId(slideElementJson.getString("element-id"));
                    slideElement.setOriginalContentCode(slideElementJson.getString("original-content-code"));
                    slideElement.setOriginalContentAsBase64(slideElementJson.getString("original-content"));
                    slideElement.setHtmlContentAsBase64(slideElementJson.getString("html-content"));

                    slide.getElements().put(slideElement.getId(), slideElement);
                }
            }

            this.presentation.getSlides().add(slide);
        }

        reader.close();

        // Creates the presentation file
        // Build the slides without the user content
        LOGGER.fine("Building presentation file");
        Velocity.init();
        VelocityContext context = new VelocityContext();
        context.put(VELOCITY_SFX_CALLBACK_TOKEN, VELOCITY_SFX_CALLBACK_CALL);
        context.put(VELOCITY_SLIDE_ID_PREFIX_TOKEN, this.template.getSlideIdPrefix());

        final StringBuffer slidesBuffer = new StringBuffer();
        final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        final Writer outputWriter = new BufferedWriter(new OutputStreamWriter(arrayOutputStream));
        FileReader slideReader;

        for(Slide s : this.presentation.getSlides()) {
            try {
                slideReader = new FileReader(s.getTemplate().getFile());
                context.put(VELOCITY_SLIDE_NUMBER_TOKEN, s.getSlideNumber());
                Velocity.evaluate(context, outputWriter, "", slideReader);

                outputWriter.flush();
                arrayOutputStream.flush();

                slidesBuffer.append(arrayOutputStream.toString("UTF8"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                arrayOutputStream.reset();
            }
        }

        Reader templateReader = null;
        try {
            templateReader = new FileReader(this.template.getFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        context = new VelocityContext();
        context.put(VELOCITY_SFX_CONTENT_DEFINER_TOKEN, VELOCITY_SFX_CONTENT_DEFINER_SCRIPT);
        context.put(VELOCITY_SFX_CALLBACK_TOKEN, VELOCITY_SFX_CALLBACK_SCRIPT);

        Velocity.evaluate(context, outputWriter, "", templateReader);

        try {
            arrayOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputWriter.flush();
        } catch (IOException e) {
            throw new PresentationException("Can not create presentation temporary file", e);
        }
        try {
            templateReader.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error while closing template stream", e);
        }

        final Document document = DOMUtils.createDocument(arrayOutputStream.toString());
        document.getElementById(this.template.getSlidesContainer())
                .empty()
                .html(slidesBuffer.toString());

        for(Slide sl : this.getPresentation().getSlides()) {
            for(SlideElement element : sl.getElements().values()) {
                document.getElementById(element.getId())
                        .empty().html(element.getHtmlContent());
            }
        }

        DOMUtils.saveDocument(document, this.getPresentation().getPresentationFile());


        LOGGER.fine("Presentation file created at " + this.presentation.getPresentationFile().getAbsolutePath());
    }

    public void openPresentation(File presentation) throws InvalidTemplateConfigurationException, InvalidTemplateException, PresentationException, InvalidPresentationConfigurationException {
        setTemplateArchiveFile(presentation);
        openPresentation();
    }

    /**
     * Add a slide to the presentation and save the presentation
     * @param template
     * @throws IOException
     */
    public Slide addSlide(SlideTemplate template, String afterSlideNumber) throws IOException, ParserConfigurationException, SAXException {
        if(template == null) throw new IllegalArgumentException("The template for creating a slide can not be null");
        Velocity.init();

        final Slide slide = new Slide(template, System.currentTimeMillis() + "");

        if(afterSlideNumber == null) {
            this.presentation.getSlides().add(slide);
        } else {
            ListIterator<Slide> slidesIterator = this.getPresentation().getSlides().listIterator();

            this.presentation.getSlideByNumber(afterSlideNumber);
            int index = -1;
            while(slidesIterator.hasNext()) {
                if(slidesIterator.next().getSlideNumber().equals(afterSlideNumber)) {
                    index = slidesIterator.nextIndex();
                    break;
                }
            }

            if(index > -1) {
                this.presentation.getSlides().add(index, slide);
            } else {
                this.presentation.getSlides().add(slide);
            }
        }


        final Reader slideFileReader = new FileReader(template.getFile());
        final ByteArrayOutputStream slideContentByte = new ByteArrayOutputStream();
        final Writer slideContentWriter = new OutputStreamWriter(slideContentByte);

        VelocityContext context = new VelocityContext();
        context.put(VELOCITY_SLIDE_ID_PREFIX_TOKEN, this.template.getSlideIdPrefix());
        context.put(VELOCITY_SLIDE_NUMBER_TOKEN, slide.getSlideNumber());
        context.put(VELOCITY_SFX_CALLBACK_TOKEN, VELOCITY_SFX_CALLBACK_CALL);

        if(template.getDynamicAttributes() != null && template.getDynamicAttributes().length > 0) {
            Scanner scanner = new Scanner(System.in);
            String value;

            for(DynamicAttribute attribute : template.getDynamicAttributes()) {
                System.out.print(attribute.getPromptMessage() + " ");
                value = scanner.nextLine();

                if(value == null || value.trim().isEmpty()) {
                    context.put(attribute.getTemplateExpression(), "");
                } else {
                    context.put(attribute.getTemplateExpression(), String.format("%1$s=\"%2$s\"", attribute.getAttribute(), value.trim()));
                }
            }
        }

        Velocity.evaluate(context, slideContentWriter, "", slideFileReader);
        slideContentWriter.flush();
        slideContentWriter.close();

        Element htmlSlide = DOMUtils.convertToNode(slideContentByte.toString("UTF8"));
        slide.setId(htmlSlide.id());

        if(afterSlideNumber == null || afterSlideNumber.isEmpty()) {
            this.presentation.getDocument()
                    .getElementById(this.template.getSlidesContainer())
                    .append(htmlSlide.outerHtml());
        } else {
            this.presentation.getDocument()
                    .getElementById(this.presentation.getSlideByNumber(afterSlideNumber).getId())
                    .after(htmlSlide.outerHtml());
        }

        this.saveTemporaryPresentation();

        return slide;
    }

    /**
     * Delete the slide with the slideNumber and save the presentation
     * @param slideNumber
     */
    public void deleteSlide(String slideNumber) throws IOException, SAXException, ParserConfigurationException {
        if(slideNumber == null) throw new IllegalArgumentException("Slide number can not be null");

        Slide slideToRemove = this.presentation.getSlideByNumber(slideNumber);
        if(slideToRemove != null) {
            this.presentation.getSlides().remove(slideToRemove);
            this.presentation.getDocument()
                    .getElementById(slideToRemove.getId()).remove();
        }

        this.saveTemporaryPresentation();
    }

    /**
     * Duplicates the given slide
     */
    public Slide duplicateSlide(Slide slide) {
        if(slide == null) throw new IllegalArgumentException("The slide to duplicate can not be null");

        final Slide copy = new Slide(slide.getTemplate(), System.currentTimeMillis() + "");
        copy.setThumbnail(slide.getThumbnail());

        // Copy the elements. Keep original IDs for now
        SlideElement copySlideElement;
        for(SlideElement slideElement : slide.getElements().values()) {
            copySlideElement = new SlideElement();
            copySlideElement.setId(slideElement.getId());
            copySlideElement.setOriginalContentCode(slideElement.getOriginalContentCode());
            copySlideElement.setOriginalContent(slideElement.getOriginalContent());
            copySlideElement.setHtmlContent(slideElement.getHtmlContent());

            copy.getElements().put(copySlideElement.getId(), copySlideElement);
        }

        // Apply the template engine for replacing dynamic elements
        final VelocityContext originalContext = new VelocityContext();
        originalContext.put(VELOCITY_SLIDE_ID_PREFIX_TOKEN, this.template.getSlideIdPrefix());
        originalContext.put(VELOCITY_SLIDE_NUMBER_TOKEN, slide.getSlideNumber());

        final VelocityContext copyContext = new VelocityContext();
        copyContext.put(VELOCITY_SLIDE_ID_PREFIX_TOKEN, this.template.getSlideIdPrefix());
        copyContext.put(VELOCITY_SLIDE_NUMBER_TOKEN, copy.getSlideNumber());

        ByteArrayOutputStream byteOutput = null;
        Writer writer = null;
        try {
            byteOutput = new ByteArrayOutputStream();
            writer = new OutputStreamWriter(byteOutput);

            String oldId, newId;

            /**
             * For each ID:
             * 1- Look for the original ID ; ie with the original slide number
             * 2- Replace each original ID by the ID of the new slide
             * 3- Store the new elements in a list
             * 4- Clear the current elements
             * 5- Create the new map of elements
             */
            List<SlideElement> copySlideElements = new ArrayList<>();
            for(String dynamicId : slide.getTemplate().getDynamicIds()) {
                Velocity.evaluate(originalContext, writer, "", dynamicId);
                writer.flush();

                oldId = new String(byteOutput.toByteArray());
                byteOutput.reset();

                if(copy.getElements().containsKey(oldId)) {
                    Velocity.evaluate(copyContext, writer, "", dynamicId);
                    writer.flush();

                    newId = new String(byteOutput.toByteArray());
                    byteOutput.reset();

                    // Change IDs
                    copySlideElement = copy.getElements().get(oldId);
                    copySlideElement.setId(newId);

                    copySlideElements.add(copySlideElement);
                }
            }

            copy.getElements().clear();
            for(SlideElement copySE : copySlideElements) {
                copy.getElements().put(copySE.getId(), copySE);
            }

            copySlideElements = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }

        return copy;
    }

    public void saveTemporaryPresentation() {
        try(final Writer writer = new FileWriter(this.presentation.getPresentationFile())) {
            writer.write(this.presentation.getDocument().html());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Build the final presentation file by writing the complete package
     */
    public void savePresentation(File presentationArchive) throws IOException {
        if(presentationArchive == null) throw new IllegalArgumentException("The presentation archive can not be null");

        LOGGER.fine("Creating the presentation configuration file");
        JsonArrayBuilder slidesJsonArray = Json.createArrayBuilder();
        JsonArrayBuilder slideElementsJsonArray;

        for(Slide slide : this.presentation.getSlides()) {
            slideElementsJsonArray = Json.createArrayBuilder();

            for(SlideElement slideElement : slide.getElements().values()) {
                slideElementsJsonArray.add(
                        Json.createObjectBuilder()
                                .add("element-id", slideElement.getId())
                                .add("original-content-code", slideElement.getOriginalContentCode())
                                .add("original-content", slideElement.getOriginalContentAsBase64())
                                .add("html-content", slideElement.getHtmlContentAsBase64())
                                .build()
                );
            }

            slidesJsonArray.add(
                    Json.createObjectBuilder()
                            .add("template-id", slide.getTemplate().getId())
                            .add("id", slide.getId())
                            .add("number", slide.getSlideNumber())
                            .add("elements", slideElementsJsonArray.build())
                            .build()
            );
        }

        JsonObject configuration = Json.createObjectBuilder()
                .add("presentation",
                        Json.createObjectBuilder()
                                .add("slides", slidesJsonArray.build()))
                .build();

        HashMap<String, Object> writerConfiguration = new HashMap<>();
        writerConfiguration.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriter writer = Json.createWriterFactory(writerConfiguration).createWriter(new FileOutputStream(new File(this.template.getFolder(), Presentation.PRESENTATION_CONFIGURATION_NAME)));
        writer.writeObject(configuration);
        writer.close();

        LOGGER.fine("Presentation configuration file created");

        LOGGER.fine("Create slides thumbnails");
        if(!this.template.getSlidesThumbnailDirectory().exists()) this.template.getSlidesThumbnailDirectory().mkdirs();
        else {
            for(File slideFile : this.template.getSlidesThumbnailDirectory().listFiles()) {
                if(slideFile.isFile()) {
                    slideFile.delete();
                }
            }
        }

        for(Slide slide : this.presentation.getSlides()) {
            LOGGER.fine("Creating thumbnail file: " + this.template.getSlidesThumbnailDirectory().getAbsolutePath() + File.separator + slide.getSlideNumber() + ".png");

            if(slide.getThumbnail() != null)
                ImageIO.write(SwingFXUtils.fromFXImage(slide.getThumbnail(), null), "png", new File(this.getTemplate().getSlidesThumbnailDirectory(), slide.getSlideNumber().concat(".png")));
        }

        LOGGER.fine("Compressing temporary file");
        ZipUtils.zip(this.template.getFolder(), presentationArchive);
        LOGGER.fine("Presentation saved");
    }
}
