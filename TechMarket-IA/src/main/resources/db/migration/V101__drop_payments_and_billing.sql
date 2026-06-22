-- TechMarket es una plataforma de CONEXION, no un e-commerce: no procesa ventas ni cobros.
-- Se eliminan los modulos vestigiales de pagos/facturacion del usuario final (metodos de pago,
-- intents, transacciones, facturas y reembolsos). El dinero de embajadores (comisiones/payouts)
-- es el programa de afiliados de la plataforma y se conserva en sus propias tablas.
-- Se conservan user_reports y user_notification_preferences (usadas por admin/notificaciones).
DROP TABLE IF EXISTS user_payment_refunds CASCADE;
DROP TABLE IF EXISTS user_invoices CASCADE;
DROP TABLE IF EXISTS user_transactions CASCADE;
DROP TABLE IF EXISTS user_payment_intents CASCADE;
DROP TABLE IF EXISTS user_payment_methods CASCADE;
