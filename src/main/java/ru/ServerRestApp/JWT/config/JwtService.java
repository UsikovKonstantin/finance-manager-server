package ru.ServerRestApp.JWT.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
    public class JwtService {

    //Секретный ключ для получения токенов
    private static final  String SECRET_KEY = "2or7vB6cLZG22+OHy6O2e2KVfxkU4cHit+kWJGF7FIpqRrySInHIWlE6Yha0PvAN";
    //Токен, используемый для выполнения запросов
    private static final Key jwtAccessSecret = getSignInKey();
    //Токен, используемый для обновление пары access и refresh токенов
    private static final Key jwtRefreshSecret = getSignInKey();

    //Кодировка секретного ключа
    private static Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //Генерация access токена
    public static String generateToken(UserDetails userDetails){ return generateToken(new HashMap<>(), userDetails); }

    public static String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*4))
                .signWith(jwtAccessSecret)
                .compact();
    }

    //Генерация refresh токена
    public static String generateRefreshToken(UserDetails userDetails)
    {
        return Jwts
                .builder()
                .setSubject(userDetails.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*24*7*3))
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public static boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private static boolean validateToken(@NonNull String token, @NonNull Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            throw expEx; //"Token expired"
        } catch (UnsupportedJwtException unsEx) {
            throw unsEx; //"Unsupported jwt"
        } catch (MalformedJwtException mjEx) {
            throw mjEx; //"Malformed jwt"
        } catch (SignatureException sEx) {
            throw sEx; //"Invalid signature"
        } catch (Exception e) {
            throw e;   //"Invalid token"
        }
    }

    public static Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private static Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) { return  extractExpiration(token).before(new Date()); }

    private Date extractExpiration(String token) { return extractClaim(token, Claims::getExpiration); }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public  String extractUsername(String token) { return extractClaim(token, Claims::getSubject); }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
}
