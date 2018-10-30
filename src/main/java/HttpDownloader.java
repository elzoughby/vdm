import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpDownloader {

    private static final int BUFFER_SIZE = 2048;

    private final URL url;
    private final String savePath;
    private String customName;

    private String fileName;
    private String contentType;
    private long fileSize;
    private long downloaded = 0;
    private boolean isRedirected = false;
    private int redirectsCount = 0;
    private List<URL> redirects;
    private int responseCode;
    private int responseTimeout = 60000;

    private HttpURLConnection conn = null;
    private Thread downloadThread = null;




    public HttpDownloader(URL url, String path) {

        this.url = url;
        this.savePath = path.replaceAll("[/\\\\]$", "");

    }

    public HttpDownloader(String url, String path) throws MalformedURLException {

        this(new URL(url), path);
    }

    private HttpURLConnection  openHttpConnection() {

        HttpURLConnection conn = null;
        boolean willRedirect;
        redirects = new ArrayList<URL>();
        redirects.add(url);

        try {

            conn = (HttpURLConnection) url.openConnection();

            do {

                conn.setConnectTimeout(responseTimeout);
                conn.setInstanceFollowRedirects(false);
                conn.connect();
                responseCode = conn.getResponseCode();
                willRedirect = false;

                if (responseCode >= 300 && responseCode <= 307 && responseCode != 306 &&
                        responseCode != HttpURLConnection.HTTP_NOT_MODIFIED) {

                    conn.disconnect();
                    URL baseURL = conn.getURL();
                    String loc = conn.getHeaderField("Location");
                    URL targetURL = null;
                    if (loc != null)
                        targetURL = new URL(baseURL, loc);

                    // Redirection should be allowed only for HTTP and HTTPS and limited to 5 redirections at most.
                    if (targetURL == null || !(targetURL.getProtocol().equals("http")
                            || targetURL.getProtocol().equals("https"))
                            || redirectsCount >= 5)
                        throw new SecurityException("illegal URL redirect");

                    willRedirect = true;
                    redirects.add(targetURL);
                    redirectsCount++;
                    conn = (HttpURLConnection) targetURL.openConnection();

                }

            } while (willRedirect);

        } catch (MalformedURLException mux) {
            System.err.println("Error: Trying to redirect to an incorrect URL :" + url.toString());
            mux.printStackTrace();
        } catch (IOException iox) {
            System.err.println("Error: Unable to open connection with " + url.toString());
            iox.printStackTrace();
        }

        return conn;
    }

    public void readDownloadInfo() {

        conn = openHttpConnection();

        if (conn != null && responseCode == HttpURLConnection.HTTP_OK) {

            fileSize = conn.getContentLength();
            contentType = conn.getContentType();
            String disposition = conn.getHeaderField("Content-Disposition");

            if (disposition != null) {
                // extracts file name from header field
                final String fileNameDelimiter = "filename=";
                int index = disposition.indexOf(fileNameDelimiter);
                if (index > 0)
                    fileName = disposition.substring(index + fileNameDelimiter.length());
            } else {
                // extracts file name from URL
                fileName = url.toString().substring(url.toString().lastIndexOf("/") + 1);
            }

        }

    }

    public void start() {

        try {

            conn.setReadTimeout(responseTimeout);

            InputStream inputStream = conn.getInputStream();
            File outputFile;
            if (customName == null || customName.equals(""))
                outputFile = new File(savePath + File.separator + fileName);
            else
                outputFile = new File(savePath + File.separator + customName);
            outputFile.getParentFile().mkdirs();
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                downloaded += bytesRead;
            }

            outputStream.close();
            inputStream.close();
            conn.disconnect();

        } catch (FileNotFoundException fnfx) {
            System.err.println("Error: Incorrect save path " + savePath);
            fnfx.printStackTrace();
        } catch (IOException iox) {
            System.err.println("Error: Connection Error");
            iox.printStackTrace();
        }

    }

    public double getProgress() {

        return (double) (downloaded/ fileSize);
    }

    public URL getURL() {

        return  url;
    }

    public String getSavePath() {

        return savePath;
    }

    public String getFileName() {

        return fileName;
    }

    public String getContentType() {

        return contentType;
    }

    public long getFileSize() {

        return fileSize;
    }

    public long getDownloaded() {

        return downloaded;
    }

    public boolean isRedirected() {

        return  isRedirected;
    }

    public int getRedirectCount() {

        return redirectsCount;
    }

    public List<URL> getRedirects() {

        return redirects;
    }

    public int getResponseTimeout() {

        return responseTimeout;
    }

    public void setResponseTimeout(int responseTimeout) {

        this.responseTimeout = responseTimeout;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }
}
