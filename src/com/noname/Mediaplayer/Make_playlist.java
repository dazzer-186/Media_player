package com.noname.Mediaplayer;

import javax.swing.*;
import java.awt.*;

public class Make_playlist {
    public static JPanel main(){
        JPanel panel = new JPanel(new GridLayout(5, 0));

        JTextField name_field = new JTextField();
        name_field.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(name_field);

        return panel;
    }

}
