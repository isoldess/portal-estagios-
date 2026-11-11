package web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.AreaService;
import web.dto.ApiDtos.AreaRequest;
import web.dto.ApiDtos.AreaResponse;

import java.util.List;

@RestController
@RequestMapping("/api/areas")
public class AreaController {
    private final AreaService areas;

    public AreaController(AreaService areas) {
        this.areas = areas;
    }

    @GetMapping
    public List<AreaResponse> listar() {
        return areas.listarTodas().stream().map(AreaResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<AreaResponse> cadastrar(@Valid @RequestBody AreaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                AreaResponse.from(areas.cadastrar(request.nome())));
    }

    @PutMapping("/{id}")
    public AreaResponse atualizar(
            @PathVariable Long id, @Valid @RequestBody AreaRequest request) {
        return AreaResponse.from(areas.atualizar(id, request.nome()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        areas.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
