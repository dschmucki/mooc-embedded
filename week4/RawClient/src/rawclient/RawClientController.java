package rawclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 *
 * @author Dominik
 */
public class RawClientController implements Initializable {
    
    @FXML
    private Label label;
    
    @FXML
    private void handleReadData(ActionEvent event) {
        try {
            Socket socket = new Socket("192.168.1.20", 8042);
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            
            osw.write("READ");
            osw.flush();
            
            int recordCount = Integer.parseInt(br.readLine());
            System.out.println("number of messages: " + recordCount);
            for (int i = 0; i < recordCount; i++) {
                System.out.println("Message received: " + br.readLine());
            }
            
        } catch (IOException ex) {
            Logger.getLogger(RawClientController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
