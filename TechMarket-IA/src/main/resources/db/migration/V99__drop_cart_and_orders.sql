-- TechMarket es una plataforma de conexion (juntar expertos con clientes finales), no un
-- e-commerce: la plataforma no procesa ventas. Se elimina el flujo carrito -> checkout -> pedido
-- introducido en V73; la conexion ocurre por chat/agenda, no por compra.
DROP TABLE IF EXISTS client_order_items CASCADE;
DROP TABLE IF EXISTS client_orders CASCADE;
DROP TABLE IF EXISTS client_cart_items CASCADE;
