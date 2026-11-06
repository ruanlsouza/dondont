package senai499.com.br.donodont.controller;

import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import senai499.com.br.donodont.model.Agendamento;
import senai499.com.br.donodont.model.Cliente;
import senai499.com.br.donodont.repository.AgendamentoRepository;
import senai499.com.br.donodont.repository.ClienteRepository;

@Controller
public class PaginaController {

    private final ClienteRepository clienteRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final PasswordEncoder passwordEncoder;

    public PaginaController(ClienteRepository clienteRepository,
                            AgendamentoRepository agendamentoRepository,
                            PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/sobre")
    public String sobre() {
        return "sobre";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String realizarLogin(@RequestParam String email,
                                @RequestParam String senha,
                                HttpSession session,
                                RedirectAttributes redirect) {
        Cliente cliente = clienteRepository.findByEmailIgnoreCase(email.trim()).orElse(null);

        if (cliente == null || !passwordEncoder.matches(senha, cliente.getSenhaHash())) {
            redirect.addFlashAttribute("erro", "E-mail ou senha inválidos.");
            return "redirect:/login";
        }

        session.setAttribute("clienteId", cliente.getId());
        session.setAttribute("clienteNome", cliente.getNome());
        session.setAttribute("clienteEmail", cliente.getEmail());
        return "redirect:/painel";
    }

    @GetMapping("/cadastro")
    public String cadastro() {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String salvarCadastro(@RequestParam String nome,
                                 @RequestParam String cpf,
                                 @RequestParam String nascimento,
                                 @RequestParam String email,
                                 @RequestParam String telefone,
                                 @RequestParam String endereco,
                                 @RequestParam String senha,
                                 @RequestParam String confirmarSenha,
                                 RedirectAttributes redirect) {
        if (!senha.equals(confirmarSenha)) {
            redirect.addFlashAttribute("erro", "As senhas não coincidem.");
            return "redirect:/cadastro";
        }

        if (clienteRepository.existsByEmailIgnoreCase(email.trim())) {
            redirect.addFlashAttribute("erro", "Este e-mail já está cadastrado.");
            return "redirect:/cadastro";
        }

        if (clienteRepository.existsByCpf(cpf.replaceAll("\\D", ""))) {
            redirect.addFlashAttribute("erro", "Este CPF já está cadastrado.");
            return "redirect:/cadastro";
        }

        try {
            Cliente cliente = new Cliente();
            cliente.setNome(nome.trim());
            cliente.setCpf(cpf.replaceAll("\\D", ""));
            cliente.setNascimento(LocalDate.parse(nascimento));
            cliente.setEmail(email.trim().toLowerCase());
            cliente.setTelefone(telefone.trim());
            cliente.setEndereco(endereco.trim());
            cliente.setSenhaHash(passwordEncoder.encode(senha));
            clienteRepository.save(cliente);
            redirect.addFlashAttribute("sucesso", "Cadastro realizado! Faça seu login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", "Não foi possível realizar o cadastro. Confira os dados.");
            return "redirect:/cadastro";
        }
    }

    @GetMapping("/painel")
    public String painel(HttpSession session, Model model) {
        String email = (String) session.getAttribute("clienteEmail");
        if (email == null) {
            return "redirect:/login";
        }
        List<Agendamento> consultas = agendamentoRepository.findByEmailIgnoreCaseOrderByDataConsultaAsc(email);
        model.addAttribute("consultas", consultas);
        return "painel";
    }

    @GetMapping("/agendamento")
    public String agendamento(HttpSession session) {
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login";
        }
        return "agendamento";
    }

    @PostMapping("/agendamento")
    public String salvarAgendamento(@RequestParam String servico,
                                    @RequestParam String data,
                                    HttpSession session,
                                    RedirectAttributes redirect) {
        String nome = (String) session.getAttribute("clienteNome");
        String email = (String) session.getAttribute("clienteEmail");

        if (email == null) {
            return "redirect:/login";
        }

        try {
            LocalDate dataConsulta = LocalDate.parse(data);
            if (dataConsulta.isBefore(LocalDate.now())) {
                redirect.addFlashAttribute("erro", "Escolha uma data a partir de hoje.");
                return "redirect:/agendamento";
            }

            Agendamento agendamento = new Agendamento();
            agendamento.setNome(nome);
            agendamento.setEmail(email);
            agendamento.setServico(servico);
            agendamento.setDataConsulta(dataConsulta);
            agendamentoRepository.save(agendamento);

            redirect.addFlashAttribute("sucesso", "Consulta solicitada com sucesso!");
            return "redirect:/painel";
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", "Não foi possível salvar o agendamento. Tente novamente.");
            return "redirect:/agendamento";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}