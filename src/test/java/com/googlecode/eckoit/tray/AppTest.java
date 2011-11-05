/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.tray;

import com.googlecode.eckoit.tray.Messages;
import com.googlecode.eckoit.tray.App;
import java.awt.MenuItem;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ryan
 */
public class AppTest {

    public AppTest() {
    }


    /**
     * Test of loadMessages method, of class App.
     */
    @Test
    public void testLoadMessages() {
        System.out.println("loadMessages");
        App instance = new App();
        Messages result = instance.loadMessages();
        assertNotNull(result);
        assertNotNull(result.getLocalMessage(Messages.APP_NAME));
        System.out.println(result.getLocalMessage(Messages.APP_NAME));
    }







}