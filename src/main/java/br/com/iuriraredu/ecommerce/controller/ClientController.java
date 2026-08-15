package br.com.iuriraredu.ecommerce.controller;

import br.com.iuriraredu.ecommerce.entity.Client;
import br.com.iuriraredu.ecommerce.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService service;

    @PostMapping
    public ResponseEntity<Client> create(@RequestBody Client client) {
        Client savedClient = service.create(client);
        return ResponseEntity.status(CREATED).body(savedClient);
    }

    @GetMapping
    public List<Client> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> update(@PathVariable Long id, @RequestBody Client client) {
        return ResponseEntity.ok(service.update(id, client));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}