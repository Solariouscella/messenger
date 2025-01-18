package org.kcgi.web.messenger.resources;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.kcgi.web.messenger.model.Message;
import org.kcgi.web.messenger.model.TranslationResponse;
import org.kcgi.web.messenger.resources.beans.MessageFilterBean;
import org.kcgi.web.messenger.service.GoogleTranslateService;
import org.kcgi.web.messenger.service.MessageService;

@Path("/messages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MessageResource {

	MessageService messageService = new MessageService();
	
	@GET
	public List<Message> getMessages(@BeanParam MessageFilterBean filterBean) {
		
		if (filterBean.getYear() > 0) {
			return messageService.getAllMessagesForYear(filterBean.getYear());
		}
		if (filterBean.getStart() >= 0 && filterBean.getSize() > 0) {
			return messageService.getAllMessagesPaginated(filterBean.getStart(), filterBean.getSize());
		}
		return messageService.getAllMessages();
	}

	@POST
	public Response addMessage(Message message, @Context UriInfo uriInfo) {
		
		Message newMessage = messageService.addMessage(message);
		String newId = String.valueOf(newMessage.getId());
		URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
		return Response.created(uri)
				.entity(newMessage)
				.build();
	}
	
	@PUT
	@Path("/{messageId}")
	public Message updateMessage(@PathParam("messageId") long id, Message message) {
		message.setId(id);
		return messageService.updateMessage(message);
	}
	
	@DELETE
	@Path("/{messageId}")
	public void deleteMessage(@PathParam("messageId") long id) {
		messageService.removeMessage(id);
	}
	
	
	@GET
	@Path("/{messageId}")
	public Message getMessage(@PathParam("messageId") long id, @Context UriInfo uriInfo) {
		Message message = messageService.getMessage(id);
		message.addLink(getUriForSelf(uriInfo, message), "self");
		message.addLink(getUriForProfile(uriInfo, message), "profile");
		message.addLink(getUriForComments(uriInfo, message), "comments");
		
		return message;
		
	}

	private String getUriForComments(UriInfo uriInfo, Message message) {
		URI uri = uriInfo.getBaseUriBuilder()
				.path(MessageResource.class)
	       		.path(MessageResource.class, "getCommentResource")
	       		.path(CommentResource.class)
	       		.resolveTemplate("messageId", message.getId())
	            .build();
	    return uri.toString();
	}

	private String getUriForProfile(UriInfo uriInfo, Message message) {
		URI uri = uriInfo.getBaseUriBuilder()
       		 .path(ProfileResource.class)
       		 .path(message.getAuthor())
             .build();
        return uri.toString();
	}

	private String getUriForSelf(UriInfo uriInfo, Message message) {
		String uri = uriInfo.getBaseUriBuilder()
		 .path(MessageResource.class)
		 .path(Long.toString(message.getId()))
		 .build()
		 .toString();
		return uri;
	}
	
	
	@Path("/{messageId}/comments")
	public CommentResource getCommentResource() {
		return new CommentResource();
	}
        
        @GET
        @Path("/translate/{targetLang}/{id}")
        public Response translateMessage(@PathParam("targetLang") String targetLang, @PathParam("id") int id) {
            // Retrieve the message from the service
            Message originalMessage = messageService.getMessageById(id);
            if (originalMessage == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Message not found for ID: " + id)
                        .build();
            }

            // Extract the original message content and author
            String messageContent = originalMessage.getContent();
            String author = originalMessage.getAuthor();

            // Translate the message
            try {
                String translatedMessage = GoogleTranslateService.translateMessage(messageContent, "en", targetLang);

                // Create a response object
                TranslationResponse response = new TranslationResponse(
                    originalMessage.getId(),
                    author,
                    messageContent,
                    translatedMessage
                );

                // Return the response
                return Response.ok(response)
                       .header("Content-Type", "application/json; charset=UTF-8")
                       .build();

            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Error occurred during translation: " + e.getMessage())
                        .build();
            }
    
        }
}
