package com.example;

import com.example.deploy.HostingerAutoDeploy;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test in Java validating HostingerAutoDeploy configuration and URLs.
 */
public class HostingerAutoDeployTest {

    @Test
    public void testHostingerAutoDeployConfiguration() {
        HostingerAutoDeploy deployer = new HostingerAutoDeploy();

        Assert.assertEquals("https://senda.fgdll.org", HostingerAutoDeploy.TARGET_DOMAIN);
        Assert.assertEquals("https://hpanel.hostinger.com/git-auto-deployments-guide/senda.fgdll.org/github/", HostingerAutoDeploy.HPANEL_GUIDE_URL);
        Assert.assertEquals("main", HostingerAutoDeploy.DEFAULT_BRANCH);

        deployer.setWebhookUrl("https://senda.fgdll.org/webhook-test");
        Assert.assertEquals("https://senda.fgdll.org/webhook-test", deployer.getWebhookUrl());

        deployer.setWebhookSecretToken("sec_12345");
        Assert.assertEquals("sec_12345", deployer.getWebhookSecretToken());

        String instructions = HostingerAutoDeploy.getSetupInstructions();
        Assert.assertNotNull(instructions);
        Assert.assertTrue(instructions.contains("senda.fgdll.org"));
        Assert.assertTrue(instructions.contains("hpanel.hostinger.com"));
    }
}
