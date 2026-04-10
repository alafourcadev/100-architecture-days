package com.architecturedays.day010.service;

import com.architecturedays.day010.model.PedidoNavideno;
import com.architecturedays.day010.model.PedidoQuirurgico;
import com.architecturedays.day010.repository.PedidoNavidenoRepository;
import com.architecturedays.day010.repository.PedidoQuirurgicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class BenchmarkService {

    private final PedidoNavidenoRepository navRepo;
    private final PedidoQuirurgicoRepository quiRepo;
    private final Random random = new Random();

    private static final String[] ESTADOS = {"PENDIENTE", "PROCESADO", "ENVIADO", "CANCELADO"};
    private static final String[] MONEDAS = {"USD", "EUR", "ARS", "BRL"};
    private static final String[] SUCURSALES = {"BA", "MX", "SP", "MAD"};
    private static final String[] CANALES = {"WEB", "MOBILE", "POS", "API"};
    private static final String[] PRIORIDADES = {"ALTA", "MEDIA", "BAJA"};
    private static final String[] TIPOS = {"RETAIL", "MAYORISTA", "B2B"};

    public BenchmarkService(PedidoNavidenoRepository navRepo,
                            PedidoQuirurgicoRepository quiRepo) {
        this.navRepo = navRepo;
        this.quiRepo = quiRepo;
    }

    @Transactional
    public Map<String, Object> benchmarkInsert(int cantidad) {
        // BEFORE: insertar en tabla con 10 indices
        List<PedidoNavideno> navideno = generarNavideno(cantidad);
        long inicioNav = System.currentTimeMillis();
        navRepo.saveAll(navideno);
        navRepo.flush();
        long duracionNav = System.currentTimeMillis() - inicioNav;

        // AFTER: insertar en tabla con 2 indices
        List<PedidoQuirurgico> quirurgico = generarQuirurgico(cantidad);
        long inicioQui = System.currentTimeMillis();
        quiRepo.saveAll(quirurgico);
        quiRepo.flush();
        long duracionQui = System.currentTimeMillis() - inicioQui;

        double mejora = ((duracionNav - duracionQui) * 100.0) / duracionNav;

        return Map.of(
                "cantidadRegistros", cantidad,
                "antesConDiezIndices", duracionNav + "ms",
                "despuesConDosIndices", duracionQui + "ms",
                "mejoraPorcentual", String.format("%.1f%%", mejora),
                "veloces", duracionNav > duracionQui
                        ? "DESPUES es " + String.format("%.1fx", (double) duracionNav / duracionQui) + " mas rapido"
                        : "Sin diferencia significativa"
        );
    }

    public Map<String, Object> benchmarkSelect(Long clienteId) {
        LocalDate inicio = LocalDate.now().minusYears(1);
        LocalDate fin = LocalDate.now();

        long t1 = System.currentTimeMillis();
        int navCount = navRepo.findByClienteIdAndFechaBetween(clienteId, inicio, fin).size();
        long duracionNav = System.currentTimeMillis() - t1;

        long t2 = System.currentTimeMillis();
        int quiCount = quiRepo.findByClienteIdAndFechaBetween(clienteId, inicio, fin).size();
        long duracionQui = System.currentTimeMillis() - t2;

        return Map.of(
                "clienteId", clienteId,
                "antesConDiezIndices", duracionNav + "ms (" + navCount + " resultados)",
                "despuesConDosIndices", duracionQui + "ms (" + quiCount + " resultados)",
                "observacion", "Los SELECTs apenas cambian. Los 8 indices extra no aportaban nada."
        );
    }

    public Map<String, Object> estadisticas() {
        return Map.of(
                "pedidosNavideno", navRepo.count(),
                "pedidosQuirurgico", quiRepo.count(),
                "indicesNavideno", 10,
                "indicesQuirurgico", 2
        );
    }

    private List<PedidoNavideno> generarNavideno(int cantidad) {
        List<PedidoNavideno> lista = new ArrayList<>(cantidad);
        for (int i = 0; i < cantidad; i++) {
            lista.add(new PedidoNavideno(
                    (long) random.nextInt(1000) + 1,
                    LocalDate.now().minusDays(random.nextInt(365)),
                    ESTADOS[random.nextInt(ESTADOS.length)],
                    BigDecimal.valueOf(random.nextInt(10000) + 100),
                    MONEDAS[random.nextInt(MONEDAS.length)],
                    SUCURSALES[random.nextInt(SUCURSALES.length)],
                    (long) random.nextInt(50) + 1,
                    CANALES[random.nextInt(CANALES.length)],
                    PRIORIDADES[random.nextInt(PRIORIDADES.length)],
                    TIPOS[random.nextInt(TIPOS.length)]
            ));
        }
        return lista;
    }

    private List<PedidoQuirurgico> generarQuirurgico(int cantidad) {
        List<PedidoQuirurgico> lista = new ArrayList<>(cantidad);
        for (int i = 0; i < cantidad; i++) {
            lista.add(new PedidoQuirurgico(
                    (long) random.nextInt(1000) + 1,
                    LocalDate.now().minusDays(random.nextInt(365)),
                    ESTADOS[random.nextInt(ESTADOS.length)],
                    BigDecimal.valueOf(random.nextInt(10000) + 100),
                    MONEDAS[random.nextInt(MONEDAS.length)],
                    SUCURSALES[random.nextInt(SUCURSALES.length)],
                    (long) random.nextInt(50) + 1,
                    CANALES[random.nextInt(CANALES.length)],
                    PRIORIDADES[random.nextInt(PRIORIDADES.length)],
                    TIPOS[random.nextInt(TIPOS.length)]
            ));
        }
        return lista;
    }
}
