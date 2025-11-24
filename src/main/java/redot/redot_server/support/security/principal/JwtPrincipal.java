package redot.redot_server.support.security.principal;

import redot.redot_server.support.jwt.token.TokenType;

public record JwtPrincipal(Long id, TokenType tokenType, Long redotAppId) {}
