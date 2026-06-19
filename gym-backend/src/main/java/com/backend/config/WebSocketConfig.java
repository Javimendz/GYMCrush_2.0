package com.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.backend.security.jwt.JwtGenerator;

/**
 * Configuración de WebSocket para la aplicación GYM Crush.
 * <p>
 * Esta clase establece la configuración necesaria para habilitar la comunicación
 * en tiempo real mediante WebSockets utilizando el protocolo STOMP (Simple Text Oriented Messaging Protocol).
 * Permite la implementación de funcionalidades como chat en vivo, notificaciones
 * push y actualizaciones en tiempo real entre el servidor y los clientes.
 * </p>
 * * <p>
 * <b>Características principales:</b>
 * <ul>
 * <li>Configuración de broker de mensajes para suscripciones</li>
 * <li>Definición de endpoints para conexión de clientes</li>
 * <li>Soporte para SockJS con fallback para navegadores antiguos</li>
 * <li>Configuración CORS para permitir conexiones desde diferentes orígenes</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Autowired
    private JwtGenerator jwtService;
     @Autowired
    private UserDetailsService userDetailsService; 
    /**
     * Configura el broker de mensajes para el manejo de suscripciones y envío de mensajes.
     * <p>
     * Establece los prefijos URL que se utilizarán para:
     * <ul>
     * <li><b>/topic</b>: Para canales públicos y broadcasts</li>
     * <li><b>/queue</b>: Para mensajes privados específicos de un usuario</li>
     * <li><b>/app</b>: Para que los clientes envíen mensajes al servidor</li>
     * </ul>
     * </p>
     *
     * @param config Registro de configuración del broker de mensajes
     */
  @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
                registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

 @Override
public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = 
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                String authHeader = accessor.getFirstNativeHeader("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    
                    //  Validar el token primero
                    if (jwtService.validateToken(token)) {
                        //  Extraer el username 
                        String username = jwtService.getUsernameFromToken(token); 

                        if (username != null) {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                            UsernamePasswordAuthenticationToken authToken = 
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            
                            accessor.setUser(authToken);
                        }
                    } else {
                        throw new MessageDeliveryException("Token JWT inválido");
                    }
                } else {
                    throw new MessageDeliveryException("Falta encabezado de autorización");
                }
            }
            return message;
        }
    });
}
}