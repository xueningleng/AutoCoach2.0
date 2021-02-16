package com.example.autocoach20.Activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.autocoach20.R;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class CommunicationActivity extends AppCompatActivity {

    TextView terminal;
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        terminal = (TextView) findViewById(R.id.terminal);
        input = (EditText) findViewById(R.id.editText);
    }

    public void sendCommand(View view) {
        if (terminal == null || input == null)
            return;

        CharSequence command = input.getText();
        appendLineToTerminal("Command: " + command);


        final Handler handler = new Handler();
        Thread thread = new Thread((new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket("cyh-pi", 65432);

                    OutputStream out = s.getOutputStream();
                    PrintWriter output = new PrintWriter(out);
                    output.println(command);
                    output.flush();

                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    final String st = input.readLine();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String oldText = terminal.getText().toString();
                            if (st.trim().length() != 0)
                                appendLineToTerminal("Response: "+st);
                        }
                    });

                    output.close();
                    out.close();
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));

        thread.start();
    }

    private void appendLineToTerminal(String text){
        if(terminal==null)
            return;

        CharSequence oldText = terminal.getText();
        CharSequence newText = oldText + "\n" + text;

        terminal.setText(newText);

    }
}