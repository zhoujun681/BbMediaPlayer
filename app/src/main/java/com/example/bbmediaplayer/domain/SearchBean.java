package com.example.bbmediaplayer.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchBean {

    /**
     * title : 女儿美国籍被指不爱国，姚明只说了1句话，记者瞬间哑口无言
     * time : 2019-08-27 06:12:24
     * src : 阿晨看游戏
     * category : null
     * pic : http://p5.qhimg.com/t012f4df7ed2c29b14f.jpg
     * url : https://tran.news.so.com/ctranscode?title=%E5%A5%B3%E5%84%BF%E7%BE%8E%E5%9B%BD%E7%B1%8D%E8%A2%AB%E6%8C%87%E4%B8%8D%E7%88%B1%E5%9B%BD%2C%E5%A7%9A%E6%98%8E%E5%8F%AA%E8%AF%B4%E4%BA%861%E5%8F%A5%E8%AF%9D%2C%E8%AE%B0%E8%80%85%E7%9E%AC%E9%97%B4%E5%93%91%E5%8F%A3%E6%97%A0%E8%A8%80&u=http%3A%2F%2Fwww.360kuai.com%2Fpc%2F9154ee25dee9cbb1d%3Fcota%3D3%26kuai_so%3D1%26sign%3D360_e39369d1%26refer_scene%3Dso_4&m=9613455c4ee458b25b5ddff2ca165784c35bcec6&q=%E5%A7%9A%E6%98%8E&360sodetail=1&src=mnews
     * weburl : http://zm.news.so.com/50652642a030a32d9ec3f6044630d296
     * content : <p>提起姚明想必很多人都不陌生，作为中国男篮第一人，一直以来他都是大众关注的焦点，如今的姚明家庭美满还有一个可爱的女儿，但一直以来，姚明女儿的美国国籍都备受争议，让不少人都质疑姚明是否爱国，为什么要让女儿成为外国籍呢？</p><p><img src=
     * gallery : {"1":"http:\/\/p1.qhimg.com\/t01a4359fe573287ba6.jpg","2":"http:\/\/p7.qhimg.com\/t0124786366c255df7b.jpg","3":"http:\/\/p8.qhimg.com\/t017e31c0363d96c0e1.jpg"}
     * addtime : 1566857544
     */

    private String title;
    private String time;
    private String src;
    private String pic;
    private String url;
    private String weburl;
    private String content;
    private int addtime;
    /**
     * status : 0
     * msg : ok
     * result : {"keyword":"姚明","num":12,"list":[{"title":"09年姚明花1.8亿买下上海队，十年过后赚了多少钱？说出来你别不信","time":"2019-08-27 05:57:08","src":"寻芹娱乐","category":null,"pic":"http://p4.qhimg.com/t01f21ed6a8a7afed1f.jpg","url":"https://tran.news.so.com/ctranscode?title=09%E5%B9%B4%E5%A7%9A%E6%98%8E%E8%8A%B11.8%E4%BA%BF%E4%B9%B0%E4%B8%8B%E4%B8%8A%E6%B5%B7%E9%98%9F%2C%E5%8D%81%E5%B9%B4%E8%BF%87%E5%90%8E%E8%B5%9A%E4%BA%86%E5%A4%9A%E5%B0%91%E9%92%B1%3F%E8%AF%B4%E5%87%BA%E6%9D%A5%E4%BD%A0%E5%88%AB%E4%B8%8D%E4%BF%A1&u=http%3A%2F%2Fwww.360kuai.com%2Fpc%2F9b6ed61acae1fdf98%3Fcota%3D3%26kuai_so%3D1%26sign%3D360_e39369d1%26refer_scene%3Dso_4&m=97ae322dfca0151af3f7bfca1f6b462e0200d065&q=%E5%A7%9A%E6%98%8E&360sodetail=1&src=mnews","weburl":"http://zm.news.so.com/4f8416a093db83664781cae5fe00b858","content":"姚明队相当的棘手，如果对手不采用对姚明的报价战术的话，很有可能就会被姚明打爆。在母队需要帮助的时候姚明站了出来。","gallery":"{\"1\":\"http:\\/\\/p4.qhimg.com\\/t01d29d8f4816bcdd2b.jpg\",\"2\":\"http:\\/\\/p4.qhimg.com\\/t017a1c838e72baecad.jpg\",\"3\":\"http:\\/\\/p7.qhimg.com\\/t019e429fd8ed9bf539.jpg\"}","addtime":1566856628},{"title":"女儿美国籍被指不爱国，姚明只说了1句话，记者瞬间哑口无言","time":"2019-08-27 06:12:24","src":"阿晨看游戏","category":null,"pic":"http://p5.qhimg.com/t012f4df7ed2c29b14f.jpg","url":"https://tran.news.so.com/ctranscode?title=%E5%A5%B3%E5%84%BF%E7%BE%8E%E5%9B%BD%E7%B1%8D%E8%A2%AB%E6%8C%87%E4%B8%8D%E7%88%B1%E5%9B%BD%2C%E5%A7%9A%E6%98%8E%E5%8F%AA%E8%AF%B4%E4%BA%861%E5%8F%A5%E8%AF%9D%2C%E8%AE%B0%E8%80%85%E7%9E%AC%E9%97%B4%E5%93%91%E5%8F%A3%E6%97%A0%E8%A8%80&u=http%3A%2F%2Fwww.360kuai.com%2Fpc%2F9154ee25dee9cbb1d%3Fcota%3D3%26kuai_so%3D1%26sign%3D360_e39369d1%26refer_scene%3Dso_4&m=9613455c4ee458b25b5ddff2ca165784c35bcec6&q=%E5%A7%9A%E6%98%8E&360sodetail=1&src=mnews","weburl":"http://zm.news.so.com/50652642a030a32d9ec3f6044630d296","content":"<p>提起姚明想必很多人都不陌生，作为中国男篮第一人，一直以来他都是大众关注的焦点，如今的姚明家庭美满还有一个可爱的女儿，但一直以来，姚明女儿的美国国籍都备受争议，让不少人都质疑姚明是否爱国，为什么要让女儿成为外国籍呢？<\/p><p><img src=","gallery":"{\"1\":\"http:\\/\\/p1.qhimg.com\\/t01a4359fe573287ba6.jpg\",\"2\":\"http:\\/\\/p7.qhimg.com\\/t0124786366c255df7b.jpg\",\"3\":\"http:\\/\\/p8.qhimg.com\\/t017e31c0363d96c0e1.jpg\"}","addtime":1566857544}]}
     */

    private int status;
    private String msg;
    private ResultBean result;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWeburl() {
        return weburl;
    }

    public void setWeburl(String weburl) {
        this.weburl = weburl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAddtime() {
        return addtime;
    }

    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * keyword : 姚明
         * num : 12
         * list : [{"title":"09年姚明花1.8亿买下上海队，十年过后赚了多少钱？说出来你别不信","time":"2019-08-27 05:57:08","src":"寻芹娱乐","category":null,"pic":"http://p4.qhimg.com/t01f21ed6a8a7afed1f.jpg","url":"https://tran.news.so.com/ctranscode?title=09%E5%B9%B4%E5%A7%9A%E6%98%8E%E8%8A%B11.8%E4%BA%BF%E4%B9%B0%E4%B8%8B%E4%B8%8A%E6%B5%B7%E9%98%9F%2C%E5%8D%81%E5%B9%B4%E8%BF%87%E5%90%8E%E8%B5%9A%E4%BA%86%E5%A4%9A%E5%B0%91%E9%92%B1%3F%E8%AF%B4%E5%87%BA%E6%9D%A5%E4%BD%A0%E5%88%AB%E4%B8%8D%E4%BF%A1&u=http%3A%2F%2Fwww.360kuai.com%2Fpc%2F9b6ed61acae1fdf98%3Fcota%3D3%26kuai_so%3D1%26sign%3D360_e39369d1%26refer_scene%3Dso_4&m=97ae322dfca0151af3f7bfca1f6b462e0200d065&q=%E5%A7%9A%E6%98%8E&360sodetail=1&src=mnews","weburl":"http://zm.news.so.com/4f8416a093db83664781cae5fe00b858","content":"姚明队相当的棘手，如果对手不采用对姚明的报价战术的话，很有可能就会被姚明打爆。在母队需要帮助的时候姚明站了出来。","gallery":"{\"1\":\"http:\\/\\/p4.qhimg.com\\/t01d29d8f4816bcdd2b.jpg\",\"2\":\"http:\\/\\/p4.qhimg.com\\/t017a1c838e72baecad.jpg\",\"3\":\"http:\\/\\/p7.qhimg.com\\/t019e429fd8ed9bf539.jpg\"}","addtime":1566856628},{"title":"女儿美国籍被指不爱国，姚明只说了1句话，记者瞬间哑口无言","time":"2019-08-27 06:12:24","src":"阿晨看游戏","category":null,"pic":"http://p5.qhimg.com/t012f4df7ed2c29b14f.jpg","url":"https://tran.news.so.com/ctranscode?title=%E5%A5%B3%E5%84%BF%E7%BE%8E%E5%9B%BD%E7%B1%8D%E8%A2%AB%E6%8C%87%E4%B8%8D%E7%88%B1%E5%9B%BD%2C%E5%A7%9A%E6%98%8E%E5%8F%AA%E8%AF%B4%E4%BA%861%E5%8F%A5%E8%AF%9D%2C%E8%AE%B0%E8%80%85%E7%9E%AC%E9%97%B4%E5%93%91%E5%8F%A3%E6%97%A0%E8%A8%80&u=http%3A%2F%2Fwww.360kuai.com%2Fpc%2F9154ee25dee9cbb1d%3Fcota%3D3%26kuai_so%3D1%26sign%3D360_e39369d1%26refer_scene%3Dso_4&m=9613455c4ee458b25b5ddff2ca165784c35bcec6&q=%E5%A7%9A%E6%98%8E&360sodetail=1&src=mnews","weburl":"http://zm.news.so.com/50652642a030a32d9ec3f6044630d296","content":"<p>提起姚明想必很多人都不陌生，作为中国男篮第一人，一直以来他都是大众关注的焦点，如今的姚明家庭美满还有一个可爱的女儿，但一直以来，姚明女儿的美国国籍都备受争议，让不少人都质疑姚明是否爱国，为什么要让女儿成为外国籍呢？<\/p><p><img src=","gallery":"{\"1\":\"http:\\/\\/p1.qhimg.com\\/t01a4359fe573287ba6.jpg\",\"2\":\"http:\\/\\/p7.qhimg.com\\/t0124786366c255df7b.jpg\",\"3\":\"http:\\/\\/p8.qhimg.com\\/t017e31c0363d96c0e1.jpg\"}","addtime":1566857544}]
         */

        private String keyword;
        private int num;
        private List<ListBean> list;

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * title : 09年姚明花1.8亿买下上海队，十年过后赚了多少钱？说出来你别不信
             * time : 2019-08-27 05:57:08
             * src : 寻芹娱乐
             * category : null
             * pic : http://p4.qhimg.com/t01f21ed6a8a7afed1f.jpg
             * url : https://tran.news.so.com/ctranscode?title=09%E5%B9%B4%E5%A7%9A%E6%98%8E%E8%8A%B11.8%E4%BA%BF%E4%B9%B0%E4%B8%8B%E4%B8%8A%E6%B5%B7%E9%98%9F%2C%E5%8D%81%E5%B9%B4%E8%BF%87%E5%90%8E%E8%B5%9A%E4%BA%86%E5%A4%9A%E5%B0%91%E9%92%B1%3F%E8%AF%B4%E5%87%BA%E6%9D%A5%E4%BD%A0%E5%88%AB%E4%B8%8D%E4%BF%A1&u=http%3A%2F%2Fwww.360kuai.com%2Fpc%2F9b6ed61acae1fdf98%3Fcota%3D3%26kuai_so%3D1%26sign%3D360_e39369d1%26refer_scene%3Dso_4&m=97ae322dfca0151af3f7bfca1f6b462e0200d065&q=%E5%A7%9A%E6%98%8E&360sodetail=1&src=mnews
             * weburl : http://zm.news.so.com/4f8416a093db83664781cae5fe00b858
             * content : 姚明队相当的棘手，如果对手不采用对姚明的报价战术的话，很有可能就会被姚明打爆。在母队需要帮助的时候姚明站了出来。
             * gallery : {"1":"http:\/\/p4.qhimg.com\/t01d29d8f4816bcdd2b.jpg","2":"http:\/\/p4.qhimg.com\/t017a1c838e72baecad.jpg","3":"http:\/\/p7.qhimg.com\/t019e429fd8ed9bf539.jpg"}
             * addtime : 1566856628
             */

            @SerializedName("title")
            private String titleX;
            @SerializedName("time")
            private String timeX;
            @SerializedName("src")
            private String srcX;
            private Object category;
            @SerializedName("pic")
            private String picX;
            @SerializedName("url")
            private String urlX;
            @SerializedName("weburl")
            private String weburlX;
            @SerializedName("content")
            private String contentX;
            private String gallery;
            @SerializedName("addtime")
            private int addtimeX;

            public String getTitleX() {
                return titleX;
            }

            public void setTitleX(String titleX) {
                this.titleX = titleX;
            }

            public String getTimeX() {
                return timeX;
            }

            public void setTimeX(String timeX) {
                this.timeX = timeX;
            }

            public String getSrcX() {
                return srcX;
            }

            public void setSrcX(String srcX) {
                this.srcX = srcX;
            }

            public Object getCategory() {
                return category;
            }

            public void setCategory(Object category) {
                this.category = category;
            }

            public String getPicX() {
                return picX;
            }

            public void setPicX(String picX) {
                this.picX = picX;
            }

            public String getUrlX() {
                return urlX;
            }

            public void setUrlX(String urlX) {
                this.urlX = urlX;
            }

            public String getWeburlX() {
                return weburlX;
            }

            public void setWeburlX(String weburlX) {
                this.weburlX = weburlX;
            }

            public String getContentX() {
                return contentX;
            }

            public void setContentX(String contentX) {
                this.contentX = contentX;
            }

            public String getGallery() {
                return gallery;
            }

            public void setGallery(String gallery) {
                this.gallery = gallery;
            }

            public int getAddtimeX() {
                return addtimeX;
            }

            public void setAddtimeX(int addtimeX) {
                this.addtimeX = addtimeX;
            }
        }
    }
}
