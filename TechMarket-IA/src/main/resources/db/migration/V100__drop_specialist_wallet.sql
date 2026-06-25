-- En el modelo de conexion, lo que un especialista cobre por su servicio es entre el y el cliente:
-- la plataforma no procesa ni custodia ese dinero. Se elimina la billetera del especialista
-- (transacciones + retiros) creada en V79/V80. Se conservan specialist_files, certificaciones,
-- ai_queries y reviews de esas mismas migraciones.
DROP TABLE IF EXISTS specialist_withdrawals CASCADE;
DROP TABLE IF EXISTS specialist_transactions CASCADE;
