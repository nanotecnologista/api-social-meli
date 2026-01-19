package com.api.social.meli.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class OpenApiOperationCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        String controllerName = handlerMethod.getBeanType().getSimpleName();
        String methodName = handlerMethod.getMethod().getName();
        
        if (requiresAuthentication(controllerName, methodName)) {
            Parameter xUserIdParam = new Parameter()
                    .in("header")
                    .name("X-user-id")
                    .description("ID do usuário autenticado")
                    .required(true)
                    .schema(new StringSchema())
                    .example("1");
            
            operation.addParametersItem(xUserIdParam);
        }
        
        return operation;
    }
    
    private boolean requiresAuthentication(String controllerName, String methodName) {
        if ("AuthController".equals(controllerName)) {
            return false;
        }
        
        if ("CategoryController".equals(controllerName)) {
            return false;
        }
        
        if ("ProductController".equals(controllerName)) {
            return !"get".equals(methodName);
        }
        
        if ("PostController".equals(controllerName)) {
            return "publish".equals(methodName) || "promoPublish".equals(methodName);
        }
        
        if ("UserController".equals(controllerName)) {
            return "activateSeller".equals(methodName) || 
                   "followUser".equals(methodName) || 
                   "unfollowUser".equals(methodName);
        }
        
        return false;
    }
}
