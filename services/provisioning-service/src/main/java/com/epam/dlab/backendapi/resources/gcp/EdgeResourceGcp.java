package com.epam.dlab.backendapi.resources.gcp;

import com.epam.dlab.auth.UserInfo;
import com.epam.dlab.backendapi.core.FileHandlerCallback;
import com.epam.dlab.backendapi.core.commands.DockerAction;
import com.epam.dlab.backendapi.core.response.handlers.EdgeCallbackHandler;
import com.epam.dlab.backendapi.resources.base.EdgeService;
import com.epam.dlab.dto.ResourceSysBaseDTO;
import com.epam.dlab.dto.aws.keyload.UploadFileAws;
import com.epam.dlab.dto.base.keyload.UploadFileResult;
import com.epam.dlab.dto.gcp.edge.EdgeInfoGcp;
import com.epam.dlab.rest.contracts.EdgeAPI;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.auth.Auth;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

import static com.epam.dlab.rest.contracts.ApiCallbacks.*;

/**
 * Provides API to manage Edge node on GCP
 */
@Path(EdgeAPI.EDGE)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class EdgeResourceGcp extends EdgeService {


    public EdgeResourceGcp() {
        log.info("{} is initialized", getClass().getSimpleName());
    }

    @POST
    @Path("/create")
    public String create(@Auth UserInfo ui, UploadFileAws dto) throws IOException {
        saveKeyToFile(dto.getEdge().getEdgeUserName(), dto.getContent());
        return action(ui.getName(), dto.getEdge(), dto.getEdge().getCloudSettings().getIamUser(), KEY_LOADER, DockerAction.CREATE);
    }

    @POST
    @Path("/start")
    public String start(@Auth UserInfo ui, ResourceSysBaseDTO<?> dto) throws JsonProcessingException {
        return action(ui.getName(), dto, dto.getCloudSettings().getIamUser(), EDGE + STATUS_URI, DockerAction.START);
    }

    @POST
    @Path("/stop")
    public String stop(@Auth UserInfo ui, ResourceSysBaseDTO<?> dto) throws JsonProcessingException {
        return action(ui.getName(), dto, dto.getCloudSettings().getIamUser(), EDGE + STATUS_URI, DockerAction.STOP);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected FileHandlerCallback getFileHandlerCallback(DockerAction action, String uuid, String user, String callbackURI) {
        return new EdgeCallbackHandler(selfService, action, uuid, user, callbackURI, EdgeInfoGcp.class, UploadFileResult.class);
    }
}
