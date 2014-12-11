/* Required Java IO, Apache HTTP Library to using this library */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

/**
 * @name Tenlua Get Direct Link Library
 * @author pycoder
 * @version 1.0
 * @since 11/12/2014
 */
public class tenlua {
    private final static String USER_AGENT = "Mozilla/5.0";
    private final static String User = "";
    private final static String Pass = "";
    private final static String Link_API = "http://api.tenlua.vn/";
    private final static String Link_API2 = "http://api2.tenlua.vn/";
    public static void main(String[] args) throws Exception 
    {
        System.out.print("Library get link tenlua.vn Version 1.0 by Pham Quoc Thang. Copyright @ 2014");
    }
    /*
    Get ID From Link
    String link = "http://tenlua.vn/direct-downloader-rar-1337e32ce00f6f03/#download1337e32ce00f6f03";
    String id = get_id(link);
    return 1337e32ce00f6f03
    */
    public static String get_id(String link)
    {
        String idfile = null;
        String splitstr = null;
        link = link.replace("#download","/download/");
        if(link.contains("folder"))
        {
            splitstr = "/folder/";
        }
        else if(link.contains("download"))
        {
            splitstr = "/download/";
        }
        String[] cat = link.split(splitstr);
        if(cat[1].contains("/"))
        {
            String[] cat2 = cat[1].split("/");
            idfile = cat2[0];
        }
        else
        {
            idfile = cat[1];
        }
        return idfile;
    }
    /* Get Session Code Login
        String session_code = login();
        if Account VIP, Get direct link vip
        if Account Free, Get direct link free
        return session login;
    */
    public static String login() throws Exception
    {
        String login_post = "[{\"a\":\"user_login\",\"user\":\""+User+"\",\"password\":\""+Pass+"\",\"permanent\":true}]";
        String response = sendPost(Link_API,login_post);
        response = response.replace("[", "");
        response = response.replace("]", "");
        response = response.replace("\"","");
        return response;
    }
    /* Get info link
        String info = get_info_link(link_file);
        Return type: name_file tlsplit file_size tlsplit link_file
    */
    public static String get_info_link(String link) throws Exception
    {
        String postData = "[{\"a\":\"filemanager_builddownload_getinfo\",\"n\":\"" + get_id(link) + "\",\"r\":0.376080396333181}]";   
        String response = sendPost(Link_API,postData);
        
        String linkfile = get_string_between(response, ":\"", "\"");
        linkfile = linkfile.replace("\\","");
        String namefile = get_string_between(response, "\"name\":\"", "\"");
        int filesize = Integer.parseInt(get_string_between(response, "\"real_size\":\"", "\""));
        filesize = filesize / 1024;
        String fsize;

        if(filesize < 1024)
        {
            fsize = filesize + " KB";
        }
        else if(filesize < 10240)
        {
            fsize = filesize / 1024 + " MB";
        }
        else
        {
            fsize = filesize / 1024 / 1024 + " GB";
        }
        return namefile + "tlsplit" + filesize + "tlsplit" + linkfile;
    }
    /* Get Direct Link VIP
        String direct_link = get_link_file(link_file);
        If Account vip, Get Direct link download maxspeed
        If Account free, Get Direct link download limit speed
        Return direct link only
    */
    public static String get_link_file(String link) throws Exception
    {
        String postData = "[{\"a\":\"filemanager_builddownload_getinfo\",\"n\":\"" + get_id(link) + "\",\"r\":0.376080396333181}]";   
        String response = sendPost(Link_API + "?sid=" + login(),postData);
        
        String[] split = response.split("dlink\":\"");
        String[] split2 = split[1].split("\",\"");
        response = split2[0];
        response = response.replace("\\/", "/");
        return response;
    }
    /* Get all link file in folder
       Return array link with info file
       Type return: name_file tenluasplit file_size tenluasplit link_file
    */
    public static String[] get_list_link_in_folder(String link_folder) throws Exception
    {
        String[] array_link = {};
        if(link_folder.contains("folder"))
        {
            String type_link = sendPost("http://api2.tenlua.vn/","[{\"a\":\"filemanager_builddownload_getinfo\",\"n\":\"" + get_id(link_folder) + "\",\"r\":0.7345157842396046}]");
            String listli = null;
            String[] arr = type_link.split("\"link\"");
            for(int x = 2; x < arr.length; x++)
            {
                if (arr[x].contains("http:"))
                {
                    String linkfile = get_string_between(arr[x], ":\"", "\"");
                    linkfile = linkfile.replace("\\","");
                    String namefile = get_string_between(arr[x], "\"name\":\"", "\"");
                    int filesize = Integer.parseInt(get_string_between(arr[x], "\"real_size\":\"", "\""));
                    filesize = filesize / 1024;
                    String fsize;

                    if(filesize < 1024)
                    {
                        fsize = filesize + " KB";
                    }
                    else if(filesize < 10240)
                    {
                        fsize = filesize / 1024 + " MB";
                    }
                    else
                    {
                        fsize = filesize / 1024 / 1024 + " GB";
                    }
                    listli = listli + "<br>" + namefile + "tlsplit" + filesize + "tlsplitTen Luatlsplit" + linkfile;
                }
            }
            array_link = listli.split("<br>");
        }
        return array_link;
    }
    public static String get_string_between(String input, String start, String end)
    {
        String output = "";
        if (input.contains(start))
        {
            String[] split = input.split(start);
            String[] split2 = split[1].split(end);
            output = split2[0];
        }
        return output;
    }
    public static String sendGet(String url) throws Exception 
    {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);

        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(
               new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
                result.append(line);
        }

        return result.toString();
    }
		// HTTP POST request
    public static String sendPost(String url, String postx) throws Exception 
    {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        HttpParams params = new BasicHttpParams();
        params.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        post.setParams(params);
        post.setHeader("User-Agent", USER_AGENT);

        StringEntity strEntity = new StringEntity(postx);
        post.setEntity(strEntity);

        HttpResponse response = client.execute(post);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
                result.append(line);
        }

        return result.toString();

    }
}
