package org.gaung.wiwokdetok.fondasikehidupan.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

public interface JwtUtil {

    Claims decodeToken(String token) throws JwtException;

    String getId(Claims payload);

    String getRole(Claims payload);
}
