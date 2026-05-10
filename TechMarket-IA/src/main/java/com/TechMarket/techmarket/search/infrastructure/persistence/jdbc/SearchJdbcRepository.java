package com.techmarket.techmarket.search.infrastructure.persistence.jdbc;

import com.techmarket.techmarket.search.api.admin.response.GlobalSearchItemResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SearchJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public SearchJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<GlobalSearchItemResponse> search(String query) {
        String pattern = "%" + query.toLowerCase() + "%";
        List<GlobalSearchItemResponse> results = new ArrayList<>();
        results.addAll(searchListings(pattern));
        results.addAll(searchTenants(pattern));
        results.addAll(searchSpecialistServices(pattern));
        results.addAll(searchCommunities(pattern));
        return results.stream().limit(25).toList();
    }

    public List<String> suggestions(String query) {
        return search(query).stream()
                .map(GlobalSearchItemResponse::titulo)
                .distinct()
                .limit(8)
                .toList();
    }

    private List<GlobalSearchItemResponse> searchListings(String pattern) {
        return jdbcTemplate.query(
                "SELECT id, title, description FROM listings "
                        + "WHERE lower(coalesce(title, '') || ' ' || coalesce(description, '')) LIKE ? "
                        + "ORDER BY created_at DESC NULLS LAST LIMIT 8",
                (rs, rowNum) ->
                        new GlobalSearchItemResponse(
                                "PROD-" + rs.getObject("id"),
                                "producto",
                                rs.getString("title"),
                                rs.getString("description"),
                                "/cliente/marketplace/productos/PROD-" + rs.getObject("id")),
                pattern);
    }

    private List<GlobalSearchItemResponse> searchTenants(String pattern) {
        return jdbcTemplate.query(
                "SELECT id, business_name, description FROM tenants "
                        + "WHERE lower(coalesce(business_name, '') || ' ' || coalesce(description, '')) LIKE ? "
                        + "ORDER BY created_at DESC NULLS LAST LIMIT 6",
                (rs, rowNum) ->
                        new GlobalSearchItemResponse(
                                "EMP-" + rs.getObject("id"),
                                "empresa",
                                rs.getString("business_name"),
                                rs.getString("description"),
                                "/cliente/empresas/EMP-" + rs.getObject("id")),
                pattern);
    }

    private List<GlobalSearchItemResponse> searchSpecialistServices(String pattern) {
        return jdbcTemplate.query(
                "SELECT id, name, description FROM specialist_services "
                        + "WHERE lower(coalesce(name, '') || ' ' || coalesce(description, '')) LIKE ? "
                        + "ORDER BY created_at DESC NULLS LAST LIMIT 6",
                (rs, rowNum) ->
                        new GlobalSearchItemResponse(
                                "SERV-" + rs.getObject("id"),
                                "servicio",
                                rs.getString("name"),
                                rs.getString("description"),
                                "/cliente/especialistas/servicios/SERV-" + rs.getObject("id")),
                pattern);
    }

    private List<GlobalSearchItemResponse> searchCommunities(String pattern) {
        return jdbcTemplate.query(
                "SELECT id, name, description FROM communities "
                        + "WHERE lower(coalesce(name, '') || ' ' || coalesce(description, '')) LIKE ? "
                        + "ORDER BY created_at DESC NULLS LAST LIMIT 5",
                (rs, rowNum) ->
                        new GlobalSearchItemResponse(
                                "COM-" + rs.getObject("id"),
                                "comunidad",
                                rs.getString("name"),
                                rs.getString("description"),
                                "/cliente/comunidades/COM-" + rs.getObject("id")),
                pattern);
    }
}
