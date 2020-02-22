/**
 * Copyright © 2016~2020 leeyazhou (coderhook@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */

package com.github.leeyazhou.crpc.core.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * @author lee
 */
public class AsciiUtil {

  public static void main(String[] args) throws Exception {
    draw("C R P C");
  }

  public static void show() {
    draw("CRPC");
  }

  public static void draw(String str) {
    int width = 80;
    int height = 12;
    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    Graphics graphics = bi.getGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());
    graphics.setColor(Color.BLACK);
    graphics.setFont(new Font("Courier", Font.BOLD, height));
    graphics.drawString(str, 5, height - 2);

    int rr = graphics.getColor().getRGB();
    for (int y = 0; y < bi.getHeight(); y++) {
      for (int x = 0; x < bi.getWidth(); x++) {
        int rgb = bi.getRGB(x, y);
        if (rgb != rr) {
          System.out.print(" ");
        } else {
          System.out.print("0");
        }
      }
      System.out.println();
    }
  }
}
