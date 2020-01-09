package ee.al32.qrcode;


import static ee.al32.qrcode.service.HttpLogoProviderService.buildLogoUrl;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.net.URL;

import ee.al32.qrcode.service.LogoProvider;
import ee.al32.qrcode.service.QRCodeGenerator;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

@Path("/qrcode")
public class QRCodeResource {
    @Inject
    QRCodeGenerator service;
    @Inject
    LogoProvider<URL> logoProvider;

    @GET
    @Produces("image/png")
    public byte[] getQRCode(@QueryParam("message") String message,
                            @QueryParam("imageUrl") String imageUrl,
                            @QueryParam("size") Integer size) {
        return service.generateQRCode(message, logoProvider.getLogo(buildLogoUrl(imageUrl)), size);
    }
}