package Trabalho_de_Graduacao.Mesa_do_Campo_Back.Service;

import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.Cliente;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Entities.DTO.CadastroClienteDTO;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.ClienteRepository;
import Trabalho_de_Graduacao.Mesa_do_Campo_Back.Repository.EnderecoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {
    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void reativaContaInativaQuandoEmailECpfConferem() {
        Cliente contaInativa = new Cliente(7, "Nome antigo", "12345678901", "cliente@email.com", "hash-antigo", "11999999999");
        contaInativa.setAtivo(false);
        contaInativa.setIdEnderecoEntrega(3);
        Cliente novoCadastro = new Cliente("Nome novo", "12345678901", "CLIENTE@email.com", "NovaSenha#123");

        when(clienteRepository.findByEmailIgnoreCase("CLIENTE@email.com")).thenReturn(Optional.of(contaInativa));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CadastroClienteDTO resultado = clienteService.createCliente(novoCadastro);

        assertTrue(resultado.contaReativada());
        assertTrue(resultado.cliente().id() == 7);
        assertTrue(contaInativa.isAtivo());
        assertEquals("Nome novo", contaInativa.getNome());
        assertEquals(3, contaInativa.getIdEnderecoEntrega());
        assertNotEquals("NovaSenha#123", contaInativa.getSenha());
    }

    @Test
    void desativarContaPreservaRegistro() {
        Cliente contaAtiva = new Cliente(4, "Maria", "12345678901", "maria@email.com", "hash", "11999999999");
        contaAtiva.setAtivo(true);
        when(clienteRepository.findByIdAndAtivoTrue(4)).thenReturn(Optional.of(contaAtiva));

        clienteService.delete(4, 4);

        ArgumentCaptor<Cliente> clienteCaptor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(clienteCaptor.capture());
        assertFalse(clienteCaptor.getValue().isAtivo());
        verify(clienteRepository, never()).deleteById(anyInt());
    }
}
