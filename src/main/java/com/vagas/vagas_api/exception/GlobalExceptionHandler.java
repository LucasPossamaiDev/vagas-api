package com.vagas.vagas_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErroResposta erro = new ErroResposta(
                status.value(),
                "Recurso não encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResposta> tratarRegraDeNegocio(
            RegraDeNegocioException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErroResposta erro = new ErroResposta(
                status.value(),
                "Violação de regra de negócio",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<ErroCampo> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ErroCampo(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        ErroResposta erro = new ErroResposta(
                status.value(),
                "Erro de validação",
                "Um ou mais campos contêm erros de validação.",
                request.getRequestURI(),
                erros
        );
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErroResposta> tratarHandlerMethodValidation(
            HandlerMethodValidationException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<ErroCampo> erros = ex.getParameterValidationResults()
                .stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(err -> new ErroCampo(
                                result.getMethodParameter().getParameterName() != null ? result.getMethodParameter().getParameterName() : "parametro",
                                err.getDefaultMessage()
                        )))
                .toList();

        ErroResposta erro = new ErroResposta(
                status.value(),
                "Erro de validação",
                "Um ou mais parâmetros contêm erros de validação.",
                request.getRequestURI(),
                erros
        );
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResposta> tratarConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<ErroCampo> erros = ex.getConstraintViolations()
                .stream()
                .map(cv -> new ErroCampo(cv.getPropertyPath().toString(), cv.getMessage()))
                .toList();

        ErroResposta erro = new ErroResposta(
                status.value(),
                "Erro de validação",
                "Um ou mais parâmetros contêm erros de validação.",
                request.getRequestURI(),
                erros
        );
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResposta> tratarTipoInvalido(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErroResposta erro = new ErroResposta(
                status.value(),
                "Parâmetro inválido",
                String.format("O parâmetro '%s' recebeu um valor inválido: '%s'", ex.getName(), ex.getValue()),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> tratarCorpoInvalido(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErroResposta erro = new ErroResposta(
                status.value(),
                "Corpo da requisição inválido",
                "O corpo da requisição é obrigatório ou está mal formatado.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> tratarViolacaoIntegridade(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErroResposta erro = new ErroResposta(
                status.value(),
                "Conflito de integridade",
                "Não foi possível processar a requisição devido a conflito de dados existentes.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> tratarErroInesperado(
            Exception ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErroResposta erro = new ErroResposta(
                status.value(),
                "Erro interno no servidor",
                "Ocorreu um erro inesperado no servidor. Tente novamente mais tarde.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(erro);
    }
}
