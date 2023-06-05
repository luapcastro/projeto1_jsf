package br.com.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.br.CPF;
import org.hibernate.validator.constraints.br.TituloEleitoral;

@Entity
public class Pessoa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotEmpty(message = "Sobrenome deve ser informado")
	@NotNull(message = "Sobrenome deve ser informado")
	@Size(min = 5, max = 50, message = "Nome deve ter entre 5 e 50 caracteres")
	private String nome;
	
	@NotEmpty(message = "Sobrenome deve ser informado")
	@NotNull(message = "Sobrenome deve ser informado")
	private String sobrenome;
	
	@DecimalMax(value = "100", message = "Idade deve ser menor que 100")
	@DecimalMin(value = "10", message = "Idade deve ser maior que 10")
	private Integer idade;
	
	@Temporal(TemporalType.DATE)
	private Date dataNascimento = new Date();
	
	private String sexo;
	
	private String[] frameworks;
	
	private Boolean ativo;
	
	private String login;
	private String senha;
	
	private String perfilUser;
	
	private String nivelProgramador;
	
	private Integer[] linguagens;
	
	private String cep;
	private String logradouro;
	private String complemento;
	private String bairro;
	private String localidade;
	private String uf;
	private String unidade;
	private String ibge;
	private String gia;
	
	@Transient // Nao e' persistente (nao criara' coluna no cando de dados)
	private Estados estado;
	
	@ManyToOne
	private Cidades cidade;
	
	@Column(columnDefinition = "text") // Tipo text: gravar arquivos em base 64
	private String fotoIconBase64;
	
	private String extensao; // Extensao da foto
	
	@Lob // Gravar arquivos no banco de dados
	@Basic(fetch = FetchType.LAZY)
	private byte[] fotoIconBase64Original;
	
	@CPF(message = "CPF inválido")
	private String cpf;
	
	@TituloEleitoral(message = "Título eleitoral inválido")
	private String titEleitoral;
	
	public Pessoa() {
		
	}
	
	
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getTitEleitoral() {
		return titEleitoral;
	}
	public void setTitEleitoral(String titEleitoral) {
		this.titEleitoral = titEleitoral;
	}
	public String getFotoIconBase64() {
		return fotoIconBase64;
	}
	public void setFotoIconBase64(String fotoIconBase64) {
		this.fotoIconBase64 = fotoIconBase64;
	}
	public String getExtensao() {
		return extensao;
	}
	public void setExtensao(String extensao) {
		this.extensao = extensao;
	}
	public byte[] getFotoIconBase64Original() {
		return fotoIconBase64Original;
	}
	public void setFotoIconBase64Original(byte[] fotoIconBase64Original) {
		this.fotoIconBase64Original = fotoIconBase64Original;
	}
	public Cidades getCidade() {
		return cidade;
	}
	public void setCidade(Cidades cidade) {
		this.cidade = cidade;
	}
	public Estados getEstado() {
		return estado;
	}
	public void setEstado(Estados estado) {
		this.estado = estado;
	}
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getLocalidade() {
		return localidade;
	}
	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String getUnidade() {
		return unidade;
	}
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	public String getIbge() {
		return ibge;
	}
	public void setIbge(String ibge) {
		this.ibge = ibge;
	}
	public String getGia() {
		return gia;
	}
	public void setGia(String gia) {
		this.gia = gia;
	}

	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public Integer[] getLinguagens() {
		return linguagens;
	}
	public void setLinguagens(Integer[] linguagens) {
		this.linguagens = linguagens;
	}
	public String getNivelProgramador() {
		return nivelProgramador;
	}
	public void setNivelProgramador(String nivelProgramador) {
		this.nivelProgramador = nivelProgramador;
	}
	public String getPerfilUser() {
		return perfilUser;
	}
	public void setPerfilUser(String perfilUser) {
		this.perfilUser = perfilUser;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Boolean getAtivo() {
		return ativo;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	public String[] getFrameworks() {
		return frameworks;
	}
	public void setFrameworks(String[] frameworks) {
		this.frameworks = frameworks;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getSobrenome() {
		return sobrenome;
	}
	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}
	public Integer getIdade() {
		return idade;
	}
	public void setIdade(Integer idade) {
		this.idade = idade;
	}
	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pessoa other = (Pessoa) obj;
		return Objects.equals(id, other.id);
	}
	
	
}
