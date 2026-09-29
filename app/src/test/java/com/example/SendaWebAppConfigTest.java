package com.example;

import com.example.deploy.SendaWebAppConfig;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for SendaWebAppConfig.
 */
public class SendaWebAppConfigTest {

    @Test
    public void testWebAppConfig() {
        SendaWebAppConfig config = new SendaWebAppConfig();
        Assert.assertEquals("https://senda.fgdll.org", config.getProductionWebappUrl());
        Assert.assertEquals("https://hpanel.hostinger.com/git-auto-deployments-guide/senda.fgdll.org/github/", SendaWebAppConfig.getHpanelGuideUrl());
        Assert.assertTrue(config.isPwaEnabled());
        Assert.assertNotNull(SendaWebAppConfig.getDeployInstructions());
        Assert.assertTrue(SendaWebAppConfig.getDeployInstructions().contains("senda.fgdll.org"));
    }
}
