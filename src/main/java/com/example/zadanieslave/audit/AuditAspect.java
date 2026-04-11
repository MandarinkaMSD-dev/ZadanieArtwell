package com.example.zadanieslave.audit;

import com.example.zadanieslave.model.entity.User;
import com.example.zadanieslave.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;

    @AfterReturning(pointcut = "@annotation(com.example.zadanieslave.audit.Auditable)", returning = "result")
    public void audit(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Auditable auditable = method.getAnnotation(Auditable.class);

        // Получаем пользователя из параметров метода (предположим, что он первый или помечен)
        User user = extractUser(joinPoint.getArgs());
        if (user == null) return; // если пользователь не передан, не логируем

        // Извлекаем ID сущности из результата (если нужно)
        UUID entityId = extractEntityId(result, auditable.entityType());

        // Сохраняем аудит
        auditLogService.log(
                auditable.action(),
                auditable.entityType().isEmpty() ? null : auditable.entityType(),
                entityId,
                user,
                auditable.details()
        );
    }

    private User extractUser(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof User) {
                return (User) arg;
            }
        }
        return null;
    }

    private UUID extractEntityId(Object result, String entityType) {
        if (result == null) return null;
        // Простейший случай: если возвращается сущность с методом getId()
        try {
            Method getId = result.getClass().getMethod("getId");
            return (UUID) getId.invoke(result);
        } catch (Exception e) {
            return null;
        }
    }
}