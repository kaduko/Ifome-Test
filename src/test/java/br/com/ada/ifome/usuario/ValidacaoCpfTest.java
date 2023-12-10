package br.com.ada.ifome.usuario;

import br.com.ada.ifome.enumeration.InstituicaoBancaria;
import br.com.ada.ifome.enumeration.TipoConta;
import br.com.ada.ifome.enumeration.TipoDeDocumento;
import br.com.ada.ifome.model.ContaBancaria;
import br.com.ada.ifome.model.Usuario;
import br.com.ada.ifome.exceptions.usuario.CpfInvalidoException;
import br.com.ada.ifome.model.documents.CNH;
import br.com.ada.ifome.model.documents.Documento;
import br.com.ada.ifome.repository.UsuarioRepository;
import br.com.ada.ifome.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ValidacaoCpfTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;


    @Test
    public void usuarioCpfInvalidoComLetra() {
        var usuario = new Usuario();
        usuario.setTipoDocumento(TipoDeDocumento.CPF);
        usuario.setNumeroDeDocumento("1234567891e");
        assertThrows(CpfInvalidoException.class, () -> usuarioService.salvar(usuario));
    }

    @Test
    public void usuarioCpfInvalidoCom12Digitos() {
        var usuario = new Usuario();
        usuario.setTipoDocumento(TipoDeDocumento.CPF);
        usuario.setNumeroDeDocumento("123456789123");
        assertThrows(CpfInvalidoException.class, () -> usuarioService.salvar(usuario));
    }

    @Test
    public void usuarioComCpfValido() {
        // Mockar ação de save
        var usuario = new Usuario();
        usuario.setTipoDocumento(TipoDeDocumento.CPF);
        usuario.setNumeroDeDocumento("04455566633");
        when(usuarioRepository.save(any())).thenReturn(usuario);
        criarDataExpedicao(usuario);
        ContaBancaria contaBancaria = new ContaBancaria();
        contaBancaria.setNumeroAgencia(1234L);
        contaBancaria.setNumeroConta(456);
        contaBancaria.setTipoConta(TipoConta.CONTA_CORRENTE);
        contaBancaria.setInstituicaoBancaria(InstituicaoBancaria.SANTANDER);
        usuario.setContasBancarias(Arrays.asList(contaBancaria));

        var usuarioSalvo = usuarioService.salvar(usuario);


        assertNotNull(usuarioSalvo);
        // Validar se foi chamado o save do repository
        verify(usuarioRepository, Mockito.times(1)).save(usuario);
    }

    private static void criarDataExpedicao(Usuario usuario) {
        LocalDate dataExpedicao = LocalDate.of(2030, Month.FEBRUARY, 28);
        Documento cnh = new CNH();
        cnh.setDataExpedicao(dataExpedicao);

        usuario.setDocumentos(List.of(cnh));
    }

}
