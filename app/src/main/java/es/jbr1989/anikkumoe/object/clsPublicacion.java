package es.jbr1989.anikkumoe.object;

import android.text.Html;
import android.text.Spanned;

import org.json.JSONException;
import org.json.JSONObject;

import es.jbr1989.anikkumoe.other.clsDate;
import es.jbr1989.anikkumoe.other.clsTexto;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class clsPublicacion {

    //region VARIABLES

    private String type;
    private Integer id_own;
    private Long fecha;

    public clsUser user;
    public clsUser user_original;
    public clsPublicacionFeed feed;

    private String my_reaction;
    private Boolean is_replicated;

    //endregion

    //region CONSTRUCTOR


    public clsPublicacion(JSONObject jPub){

        try {this.type=jPub.getString("type");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.id_own=jPub.getInt("id_own");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.fecha= jPub.getLong("fecha");}
        catch (JSONException ex){ex.printStackTrace();}

        try {
            JSONObject user=jPub.getJSONObject("user");
            this.user= new clsUser(user);
        }
        catch (JSONException ex){ex.printStackTrace();}

        if (!type.equals("ORI")) {
            try {
                JSONObject user_original = jPub.getJSONObject("user_original");
                this.user_original = new clsUser(user_original);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        try {
            JSONObject feed=jPub.getJSONObject("feed");
            this.feed= new clsPublicacionFeed();

            try {this.feed.id=feed.getInt("id");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.feed.spoiler= feed.getBoolean("spoiler");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.feed.texto= feed.getString("texto"); this.feed.texto_html = clsTexto.toHTML(this.feed.texto);}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.feed.imagen= feed.getString("imagen"); if(this.feed.imagen.equalsIgnoreCase("null"))this.feed.imagen="";}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.feed.video= feed.getString("video"); if(this.feed.video.equalsIgnoreCase("null"))this.feed.video=""; }
            catch (JSONException ex){ex.printStackTrace();}

            try {this.feed.fecha= feed.getLong("fecha");}
            catch (JSONException ex){ex.printStackTrace();}

            if (feed.getString("anime")!="null") {

                try {
                    JSONObject anime = feed.getJSONObject("anime");
                    this.feed.anime = new clsPublicacionFeedAnime();

                    if (anime != null) {

                        try {
                            this.feed.anime.id = anime.getInt("id");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                        try {
                            this.feed.anime.titulo = anime.getString("titulo");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                        try {
                            this.feed.anime.portada = anime.getString("portada");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            try {this.feed.activity_type= feed.getString("activity_type");}
            catch (JSONException ex){ex.printStackTrace();}

            if (feed.getString("activity_data")!="null"){

                try {
                    JSONObject activity_data = feed.getJSONObject("activity_data");
                    this.feed.activity_data = new clsPublicacionFeedActivity();

                        try {
                            this.feed.activity_data.id = activity_data.getInt("id");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                        try {
                            this.feed.activity_data.name = activity_data.getString("name");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            try {
                JSONObject stats=feed.getJSONObject("stats");
                this.feed.stats= new clsPublicacionFeedStats();

                try {
                    JSONObject reactions=stats.getJSONObject("reactions");
                    this.feed.stats.reactions= new clsPublicacionFeedStatsReactions();

                    try {this.feed.stats.reactions.like=reactions.getInt("like");}
                    catch (JSONException ex){ex.printStackTrace();}

                    try {this.feed.stats.reactions.love=reactions.getInt("love");}
                    catch (JSONException ex){ex.printStackTrace();}

                    try {this.feed.stats.reactions.haha=reactions.getInt("haha");}
                    catch (JSONException ex){ex.printStackTrace();}

                    try {this.feed.stats.reactions.wow=reactions.getInt("wow");}
                    catch (JSONException ex){ex.printStackTrace();}

                    try {this.feed.stats.reactions.sorry=reactions.getInt("sorry");}
                    catch (JSONException ex){ex.printStackTrace();}

                    try {this.feed.stats.reactions.anger=reactions.getInt("anger");}
                    catch (JSONException ex){ex.printStackTrace();}

                }
                catch (JSONException ex){ex.printStackTrace();}

                try {this.feed.stats.reactions_count=stats.getInt("reactions_count");}
                catch (JSONException ex){ex.printStackTrace();}

                try {this.feed.stats.shared= stats.getString("shared");}
                catch (JSONException ex){ex.printStackTrace();}

                try {this.feed.stats.comments= stats.getString("comments");}
                catch (JSONException ex){ex.printStackTrace();}

            }
            catch (JSONException ex){ex.printStackTrace();}
        }
        catch (JSONException ex){ex.printStackTrace();}

        try {this.my_reaction= jPub.getString("my_reaction");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.is_replicated= jPub.getBoolean("is_replicated");}
        catch (JSONException ex){ex.printStackTrace();}

    }

    //endregion

    //region GETTERS


    public String getType() {
        return type;
    }

    public Integer getId_own() {
        return id_own;
    }

    public Long getFecha() {
        return fecha;
    }

    public clsUser getUser() {
        return user;
    }

    public clsUser getUser_original() {
        return user_original;
    }

    public clsPublicacionFeed getFeed() {
        return feed;
    }

    public String getMy_reaction() {
        return (my_reaction);
    }

    public Boolean getIs_replicated() {
        return is_replicated;
    }

    //endregion

    //region SETTERS

    public void setMy_reaction(String my_reaction) {
        this.my_reaction = my_reaction;
    }

    //endregion

    //region funciones

    public String getUrlUser(String ROOT_URL){
        String url="";

        if (this.type.equalsIgnoreCase("REP")==Boolean.FALSE) {
            url=ROOT_URL+"user/" + this.user.getUsuario();
        }else{
            url=ROOT_URL+"user/" + this.user_original.getUsuario();
        }

        return url;
    }

    //endregion


    public class clsPublicacionFeed{
        private Integer id;
        private Boolean spoiler;
        private String texto;
        private String texto_html;
        private String imagen="";
        private String video="";
        private Long fecha;

        private String activity_type;
        public clsPublicacionFeedActivity activity_data;

        public clsPublicacionFeedAnime anime;
        public clsPublicacionFeedStats stats;

        public Integer getId() {
            return id;
        }

        public Boolean getSpoiler() {
            return spoiler;
        }

        public String getTexto() {
            return texto;
        }

        public String getTextoHtml(){ return texto_html;}

        public String getImagen() {
            return imagen;
        }

        public String getVideo() {
            return video;
        }

        public String getIdVideo(){
            return video.split("&")[0].replace("https://www.youtube.com/watch?v=","");
        }

        public Long getFecha() {
            return fecha;
        }

        public clsPublicacionFeedAnime getAnime() {
            return anime;
        }

        public clsPublicacionFeedStats getStats() {
            return stats;
        }

        public Spanned getHTMLTexto() {
            return Html.fromHtml(clsTexto.bbcode(texto));
        }

        public String getActivity_type() {
            return activity_type;
        }

        public clsPublicacionFeedActivity getActivity_data() {
            return activity_data;
        }
    }

    public class clsPublicacionFeedAnime{
        private Integer id;
        private String titulo;
        private String portada;

        public Integer getId() {
            return id;
        }

        public String getTitulo() {
            return titulo;
        }

        public String getPortada() {
            return portada;
        }
    }

    public class clsPublicacionFeedStats{
        public clsPublicacionFeedStatsReactions reactions;
        private Integer reactions_count;
        private String shared;
        private String comments;

        public clsPublicacionFeedStatsReactions getReactions() {
            return reactions;
        }

        public Integer getReactions_count() {
            return reactions_count;
        }

        public String getShared() {
            return shared;
        }

        public String getComments() {
            return comments;
        }

    }

    public class clsPublicacionFeedStatsReactions{
        private Integer like;
        private Integer love;
        private Integer haha;
        private Integer wow;
        private Integer sorry;
        private Integer anger;

        public Integer getLike() {
            return like;
        }

        public Integer getLove() {
            return love;
        }

        public Integer getHaha() {
            return haha;
        }

        public Integer getWow() {
            return wow;
        }

        public Integer getSorry() {
            return sorry;
        }

        public Integer getAnger() {
            return anger;
        }
    }

    public class clsPublicacionFeedActivity{
        private Integer id;
        private String name;

        public Integer getId() {
            return id;
        }
        public String getName() {
            return name;
        }

        public String getHtml(Long ldate) {
            clsDate oDate = new clsDate();
            String html = "";
            html+="<p style=\"font-size: 20px\">Tiene una Waifu: <a href=\"http://www.anilista.com/personaje/"+id+"/"+name+"\" target=\"_blank\" style=\"white-space=nowrap;\"><b>"+name+"</b></a></p>";
            html+="<p style=\"margin: 10px\">"+oDate.getDate(ldate)+"</p>";
            html+="<img src=\"http://www.anilista.com/img/dir/personaje/regular/"+id+".jpg\">";

            return html;
        }
    }

}
