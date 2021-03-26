package com.mbds.grails

import grails.converters.JSON
import grails.converters.XML
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured

import javax.servlet.http.HttpServletResponse

@Secured('ROLE_ADMIN')
class ApiController {

    AnnonceService annonceService
    SpringSecurityService springSecurityService
    UserService userService

//    GET / PUT / PATCH / DELETE
//    url : localhost:8081/projet/api/annonce(s)/{id}
    def annonce() {
        switch (request.getMethod()) {
            case "GET":
                if (!params.id)
                    return response.status = HttpServletResponse.SC_BAD_REQUEST
                def annonceInstance = Annonce.get(params.id)
                if (!annonceInstance)
                    return response.status = HttpServletResponse.SC_NOT_FOUND
                response.withFormat {
                    xml { render annonceInstance as XML }
                    json { render annonceInstance as JSON }
                }
                serializeData(annonceInstance, request.getHeader("Accept"))
                break
            case "PUT":
                if (!params.id)
                    return response.status = HttpServletResponse.SC_BAD_REQUEST
                def annonceInstance = Annonce.get(params.id)
                def annonceJson = request.getJSON()
                annonceInstance.title = annonceJson.title
                annonceInstance.description = annonceJson.description
                annonceInstance.price = Double.parseDouble(""+annonceJson.price)
                annonceService.save(annonceInstance)
                if (!annonceInstance)
                    return response.status = HttpServletResponse.SC_NOT_FOUND
                response.withFormat {
                    xml { render annonceInstance as XML }
                    json { render annonceInstance as JSON }
                }
                serializeData(annonceInstance, request.getHeader("Accept"))
                break
            case "PATCH":
                if (!params.id)
                    return response.status = HttpServletResponse.SC_BAD_REQUEST
                def annonceInstance = Annonce.get(params.id)
                def annonceJson = request.getJSON()
                if(annonceJson.title!=null){
                    annonceInstance.title = annonceJson.title
                }
                if(annonceJson.description!=null){
                    annonceInstance.description = annonceJson.description
                }
                if(annonceJson.price!=null){
                    annonceInstance.price = Double.parseDouble(""+annonceJson.price)
                }
                annonceService.save(annonceInstance)
                if (!annonceInstance)
                    return response.status = HttpServletResponse.SC_NOT_FOUND
                response.withFormat {
                    xml { render annonceInstance as XML }
                    json { render annonceInstance as JSON }
                }
                serializeData(annonceInstance, request.getHeader("Accept"))
                break
            case "DELETE":
                if (!params.id )
                    return response.status = HttpServletResponse.SC_BAD_REQUEST
                def annonceInstance = Annonce.get(params.id)
                if (!annonceInstance)
                    return response.status = HttpServletResponse.SC_NOT_FOUND
                response.withFormat {
                    xml { render annonceInstance as XML }
                    json { render annonceInstance as JSON }
                }
                annonceService.delete(params.id)
                return response.status = HttpServletResponse.SC_OK
                break
            default:
                return response.status = HttpServletResponse.SC_METHOD_NOT_ALLOWED
                break
        }
        return response.status = HttpServletResponse.SC_NOT_ACCEPTABLE
    }

//    GET / POST
    def annonces() {
        switch (request.getMethod()) {
            case "POST":
                def currentUser
                def id
                if (springSecurityService.isLoggedIn()) {
                    currentUser = springSecurityService.getCurrentUser()
                    id=currentUser.id
                }

                def user = User.get(id)
                def annonceJson = request.getJSON()
                annonceJson.each {
                    annonce ->
                    def annonceInstance =new Annonce(
                            title:annonce.title,
                            description:annonce.description,
                            price:annonce.price
                    )
                    annonceInstance.addToIllustrations(new Illustration(filename: "grails.svg"))
                    user.addToAnnonces(annonceInstance)
                }
                user.save(flush: true, failOnError: true)
                break
            case "GET":

                def annoncesInstance = Annonce.getAll()
                if (!annoncesInstance)
                    return response.status = HttpServletResponse.SC_NOT_FOUND
                response.withFormat {
                    xml { render annoncesInstance as XML }
                    json { render annoncesInstance as JSON }
                }
                serializeData(annoncesInstance, request.getHeader("Accept"))
                break
            default:
                return response.status = HttpServletResponse.SC_METHOD_NOT_ALLOWED
                break
        }
        return response.status = HttpServletResponse.SC_NOT_ACCEPTABLE
    }

//    GET / PUT / PATCH / DELETE
    def user() {
        switch (request.getMethod()) {
            case "GET":
                if (!params.id)
                    return response.status = HttpServletResponse.SC_BAD_REQUEST
                def userInstance = User.get(params.id)
                if (!userInstance)
                    return response.status = HttpServletResponse.SC_NOT_FOUND
                response.withFormat {
                    xml { render userInstance as XML }
                    json { render userInstance as JSON }
                }
                serializeData(userInstance, request.getHeader("Accept"))
                break
            case "PUT":
                if (!params.id)
                    return response.status = HttpServletResponse.SC_BAD_REQUEST
                def userInstane = User.get(params.id)
                def userJson = request.getJSON()
                userInstane.username = userJson.username
                userInstane.password = userJson.password
                userService.save(userInstane)
                if (!userInstane)
                    return response.status = HttpServletResponse.SC_NOT_FOUND
                response.withFormat {
                    xml { render userInstane as XML }
                    json { render userInstane as JSON }
                }
                serializeData(userInstane, request.getHeader("Accept"))
                break

        }
        return response.status = HttpServletResponse.SC_NOT_ACCEPTABLE
    }

//    GET / POST
    def users() {

    }

    def serializeData(object, format)
    {
        switch (format)
        {
            case 'json':
            case 'application/json':
            case 'text/json':
                render object as JSON
                break
            case 'xml':
            case 'application/xml':
            case 'text/xml':
                render object as XML
                break
        }
    }
}
