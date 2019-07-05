package test.brava.logic;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.igc.be.api.core.BravaConnection;
import com.igc.be.api.core.Providers;
import com.igc.be.api.core.cache.CacheFile;
import com.igc.be.api.core.cache.UploadableFileCacheQuery;
import com.igc.be.api.core.cache.UploadableFileCacheResponse;
import com.igc.be.api.core.exception.BravaResponseException;
import com.igc.be.api.core.rendition.Composition;
import com.igc.be.api.core.rendition.RenditionRequest;
import com.igc.be.api.core.rendition.RenditionResponse;
import com.igc.be.api.core.rendition.RenditionType;

public class BravaConnector {

    private String url;

    public BravaConnector(String url) {
        this.setUrl(url);
    }

    public void run() {
        if (url == null) {
            return;
        }

        final Path filePath = Paths.get("src", "main", "resource", "CCITT_1.TIF" );

        try {
            BravaConnection conn = Providers.Connection().create(this.url);

            String sessionId = this.getSssionId(conn);
            System.out.println(sessionId);

            CacheFile cache = this.upload(conn, filePath);
            System.out.println(cache);

            String compositionId = this.createRendition(conn, cache);

            System.out.println("ID : " + compositionId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CacheFile upload(BravaConnection conn, Path path) {
        try {
            final String id = "9999999999";
            CacheFile file = Providers.Cache().createCacheFile(id);
            file.setFilename(path.getFileName().toString());
            file.setVersion("1.0");

            UploadableFileCacheQuery query = conn.createUploadableCacheQuery(file);
            UploadableFileCacheResponse queryresponse = query.getResponse();

            if (!queryresponse.isUploaded()) {
                InputStream inputFile = new FileInputStream(path.toFile());
                queryresponse.getUploader().uploadChunked(inputFile);
            }

            return file;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getSssionId(BravaConnection conn) throws BravaResponseException {
        return conn.getSessionId();
    }

    private String createRendition(BravaConnection conn, CacheFile cache) throws BravaResponseException {
        Composition composition = Providers.Rendition().createComposition(RenditionType.HTML);

        composition.getFragmentSequence().addFileRendition(Providers.Rendition().createFileRendition(cache));
        RenditionRequest publicationRequest = conn.createRenditionRequest();
        publicationRequest.getCompositions().addComposition(composition);

        RenditionResponse publicationResponse = publicationRequest.getResponse();
        return publicationResponse.getCompositionResponse(composition).getId();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
