package Login;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

public class MyTextArea extends JTextArea {
   Image backgroundChat;

   public MyTextArea(String imgStr) {
      backgroundChat = Toolkit.getDefaultToolkit().getImage(imgStr);
      setForeground(new Color(0, 0, 0));
      setDisabledTextColor(new Color(0, 0, 0));
      setOpaque(false);
      setBorder(new BevelBorder(BevelBorder.LOWERED, Color.LIGHT_GRAY, Color.LIGHT_GRAY));
      setLineWrap(true);
      setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 20));
   }

   public void paintComponent(Graphics g) {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
      g.drawImage(backgroundChat, 0, 0, getWidth(), getHeight(), this);

      super.paintComponent(g);
   }
}