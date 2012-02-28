/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package utils;

/**
 *
 * @author leo
 */
public class ColorHelper {
  public static boolean isValidColor(int rgb){
    return rgb >= 0 && rgb <= 0xFFFFFF;
  }
  public static boolean isValidColorComponent(int component){
    return component >= 0 && component <= 0xFF;
  }
  public static boolean isValidColor(int r, int g, int b){
    return isValidColorComponent(r) && isValidColorComponent(g)
        && isValidColorComponent(b);
  }
  public static int[] decompose(int color){
    int[] rgb = new int[3];
    int temp = color;
    try{
      for(int i = 0; i < 3; i++){
        rgb[2-i] = temp % 0x100;
        temp = temp / 0x100;
      }
    }catch(ArithmeticException ex){
      //let's do nothing: this is just 'cause the color if of the format 0x0000XX
    }
    //if there were other codes before the red component they are just cut
    return rgb;
  }
}
