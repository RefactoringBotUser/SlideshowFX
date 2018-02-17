# Releasing a version of SlideshowFX

In order to release a version of SlideshowFX, the following steps need to be completed successfully.

## Step 1: Updating files

This step consists in updating all files containing the `@@NEXT-VERSION@@` token. These files are particularly:
- Java source files (*.java)
- The changelog log file (`CHANGELOG.textile`)

The changes must then be committed and pushed.

## Step 2: updating version numbers

**Warning:** this step must be performed on the clean git repository, meaning no files have been modified without being
pushed.

The versions in `pom.xml` files must then be updated to **non** snapshots versions. In order to do so, the following 
maven command must be executed:

```
mvn clean release:update-versions
``` 

The command will prompt for the version numbers to apply. For the next development versions, the value must be the 
**same** as the releasing version.

If a `release.properties` file is present in the root folder of the project, it can be used using:

```
mvn clean release:update-versions --batch-mode
```

## Step 3: reporting code to production branch

The development version is located on the git branch `origin/development`. The code on this branch must be reported 
on the the `origin/master` using the following command:

```
git merge development --no-commit --squash
```

If some conflicts appear, they must be resolved. Normally the code from the development branch should be accepted in 
order to resolve those conflicts.

## Step 4: committing

Once the previous steps have been performed successfully, the code can be committed:

```
git commit -m "Releasing version XX.YY"
git tag vXX.YY
```

## Step 5: pushing

Once previous steps are performed successfully, the code can be pushed:

```
git push origin --tags
```