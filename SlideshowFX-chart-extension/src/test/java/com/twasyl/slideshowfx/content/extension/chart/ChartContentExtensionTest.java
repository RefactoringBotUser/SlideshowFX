package com.twasyl.slideshowfx.content.extension.chart;

import org.junit.BeforeClass;
import org.mockito.Mockito;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.spy;

public class ChartContentExtensionTest {

    private static ChartContentExtension chartContentExtensionMock;

    @BeforeClass
    public static void setUp() {
        chartContentExtensionMock = spy(ChartContentExtension.class);

    }
}
