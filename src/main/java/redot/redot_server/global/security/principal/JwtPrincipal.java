package redot.redot_server.global.security.principal;

import redot.redot_server.global.jwt.token.TokenType;

public record JwtPrincipal(Long id, TokenType tokenType, Long redotAppId) {}
