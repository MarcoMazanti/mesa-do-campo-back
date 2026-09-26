package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Controller.Advice;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.External.ErrorResponse;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RegistroInexistenteException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.RequisicaoIncompletaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Exception.SolicitacaoNegadaException;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service.Security.CryptoException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CryptoException.class)
    public ResponseEntity<Object> handleCryptoException(CryptoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now()));
    }

    // Erro 400 - Bad Request
    @ExceptionHandler(RequisicaoIncompletaException.class)
    public ResponseEntity<Object> handleRequisicaoIncompletaException(RequisicaoIncompletaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now()));
    }

    // Erro 401 - Unauthorized
    @ExceptionHandler(SolicitacaoNegadaException.class)
    public ResponseEntity<Object> handleSolicitacaoNegadaException(SolicitacaoNegadaException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), LocalDateTime.now()));
    }

    // Erro 404 - Not Found
    @ExceptionHandler(RegistroInexistenteException.class)
    public ResponseEntity<Object> handleRegistroInexistenteException(RegistroInexistenteException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now()));
    }

    // Erro 500 - Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno no servidor.", LocalDateTime.now()));
    }
}
