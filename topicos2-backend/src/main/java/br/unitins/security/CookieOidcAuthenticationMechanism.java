package br.unitins.security;

import java.util.Set;

import io.quarkus.arc.profile.UnlessBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.annotation.Priority;

import io.quarkus.oidc.runtime.OidcAuthenticationMechanism;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.quarkus.vertx.http.runtime.security.HttpCredentialTransport;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;

@Alternative
@Priority(1)
@ApplicationScoped
@UnlessBuildProfile("test")
public class CookieOidcAuthenticationMechanism implements HttpAuthenticationMechanism {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";

    @Inject
    OidcAuthenticationMechanism oidcAuthenticationMechanism;

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context,
            IdentityProviderManager identityProviderManager) {
        if (context.request().getHeader("Authorization") == null) {
            Cookie cookie = context.request().getCookie(ACCESS_TOKEN_COOKIE);
            if (cookie != null) {
                context.request().headers().set("Authorization", "Bearer " + cookie.getValue());
            }
        }
        return oidcAuthenticationMechanism.authenticate(context, identityProviderManager);
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        return oidcAuthenticationMechanism.getChallenge(context);
    }

    @Override
    public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
        return oidcAuthenticationMechanism.getCredentialTypes();
    }

    @Override
    public Uni<HttpCredentialTransport> getCredentialTransport(RoutingContext context) {
        return oidcAuthenticationMechanism.getCredentialTransport(context);
    }

    @Override
    public int getPriority() {
        return oidcAuthenticationMechanism.getPriority();
    }
}
