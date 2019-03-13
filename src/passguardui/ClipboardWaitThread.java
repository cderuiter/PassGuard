/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passguardui;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;


/**
 *
 * @author yusuf
 */
public class ClipboardWaitThread extends Thread{
    
    @Override
    public void run(){
           
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            
        } 
        
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection emptySelection = new StringSelection("");
        clipboard.setContents(emptySelection, emptySelection);
    }
    
}
