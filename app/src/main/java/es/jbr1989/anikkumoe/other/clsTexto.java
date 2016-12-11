package es.jbr1989.anikkumoe.other;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jbr1989 on 13/03/2016.
 */
public class clsTexto {

    public clsTexto(){}

    public static String recortar(String text, int pos){
        if (text.length()>pos) text=text.substring(0,pos)+"...";
        return text;
    }

    public static String bbcode(String text) {
        String html = text;

        Map<String,String> bbMap = new HashMap<String , String>();

        bbMap.put("(\r\n|\r|\n|\n\r)", "<br/>");
        bbMap.put("\\[b\\](.+?)\\[/b\\]", "<strong>$1</strong>");
        bbMap.put("\\[i\\](.+?)\\[/i\\]", "<span style='font-style:italic;'>$1</span>");
        bbMap.put("\\[u\\](.+?)\\[/u\\]", "<span style='text-decoration:underline;'>$1</span>");
        bbMap.put("\\[h1\\](.+?)\\[/h1\\]", "<h1>$1</h1>");
        bbMap.put("\\[h2\\](.+?)\\[/h2\\]", "<h2>$1</h2>");
        bbMap.put("\\[h3\\](.+?)\\[/h3\\]", "<h3>$1</h3>");
        bbMap.put("\\[h4\\](.+?)\\[/h4\\]", "<h4>$1</h4>");
        bbMap.put("\\[h5\\](.+?)\\[/h5\\]", "<h5>$1</h5>");
        bbMap.put("\\[h6\\](.+?)\\[/h6\\]", "<h6>$1</h6>");
        bbMap.put("\\[quote\\](.+?)\\[/quote\\]", "<blockquote>$1</blockquote>");
        bbMap.put("\\[p\\](.+?)\\[/p\\]", "<p>$1</p>");
        bbMap.put("\\[p=(.+?),(.+?)\\](.+?)\\[/p\\]", "<p style='text-indent:$1px;line-height:$2%;'>$3</p>");
        bbMap.put("\\[center\\](.+?)\\[/center\\]", "<div align='center'>$1");
        bbMap.put("\\[align=(.+?)\\](.+?)\\[/align\\]", "<div align='$1'>$2");
        bbMap.put("\\[color=(.+?)\\](.+?)\\[/color\\]", "<span style='color:$1;'>$2</span>");
        bbMap.put("\\[size=(.+?)\\](.+?)\\[/size\\]", "<span style='font-size:$1;'>$2</span>");
        bbMap.put("\\[img\\](.+?)\\[/img\\]", "<img style='max-width:100%;' src='$1' />");
        bbMap.put("\\[img=(.+?),(.+?)\\](.+?)\\[/img\\]", "<img width='$1' height='$2' src='$3' />");
        bbMap.put("\\[email\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$1</a>");
        bbMap.put("\\[email=(.+?)\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$2</a>");
        bbMap.put("\\[url\\](.+?)\\[/url\\]", "<a href='$1'>$1</a>");
        bbMap.put("\\[url=(.+?)\\](.+?)\\[/url\\]", "<a href='$1'>$2</a>");
        bbMap.put("\\[youtube\\](.+?)\\[/youtube\\]", "<object width='640' height='380'><param name='movie' value='http://www.youtube.com/v/$1'></param><embed src='http://www.youtube.com/v/$1' type='application/x-shockwave-flash' width='640' height='380'></embed></object>");
        bbMap.put("\\[video\\](.+?)\\[/video\\]", "<video src='$1' />");

        for (Map.Entry entry: bbMap.entrySet()) {
            html = html.replaceAll(entry.getKey().toString(), entry.getValue().toString());
        }

        return html;
    }

    public static String toHTML(String text){
        String html=text;

        html=html.replaceAll("(^|\\s)#(\\w*[a-zA-Z_Ã±Ã¡Ã©Ã\u00ADÃ³Ãº]+\\w*)", "$1<a href=\"hashtag:$2\" class=\"hashtag\">#$2</a>");
        html=html.replaceAll("(^|\\s)\\@(\\w*[A-Za-z0-9-_.]+\\w*)","$1<a href=\"user:$2\">@$2</a>");
        html=html.replaceAll("(\\b(https?|ftp):\\/\\/[A-z0-9+&@#\\/%?=~_|!:,.;-]*[-A-z0-9+&@#\\/%=~_|])","<a href=\"$1\">$1</a>");
        html=html.replaceAll("(\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,6})","<a href=\"mailto:$1\">$1</a>");
        html=html.replaceAll("(?:\r\n|\r|\n)", "<br />");

        //Mostrar imagenes incrustadas en el texto
        html=html.replaceAll("'","\"");
        html=html.replaceAll("src=\"//","src=\"http://");

        return bbcode(html);
    }

}
