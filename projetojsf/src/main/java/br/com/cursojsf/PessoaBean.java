package br.com.cursojsf;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;

import br.com.dao.DAOGeneric;
import br.com.entidades.Cidades;
import br.com.entidades.Estados;
import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;
import br.com.repository.IDaoPessoa;
import br.com.repository.IDaoPessoaImpl;
import net.bootsfaces.component.selectOneMenu.SelectOneMenu;

@javax.faces.view.ViewScoped
@Named(value = "pessoaBean")
public class PessoaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Pessoa pessoa = new Pessoa();
	
	@Inject
	private DAOGeneric<Pessoa> daoGeneric;
	
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	
	@Inject
	private IDaoPessoa iDaoPessoa;
	
	@Inject
	private JPAUtil jpaUtil;
	
	private List<SelectItem> estados;
	private List<SelectItem> cidades;
	
	private Part arquivoFoto;
	
	public String salvar() throws IOException {
		// Processa imagem
		byte[] imagemByte = null;
		
		if (arquivoFoto != null) {
			imagemByte = getByte(arquivoFoto.getInputStream());
		}
		
		if (imagemByte != null && imagemByte.length > 0) {
			
			pessoa.setFotoIconBase64Original(imagemByte);
			
			// Transforma em um bufferimage
			BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagemByte));
			
			// Pega o tipo da imagem
			int type = bufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
			
			int largura = 200;
			int altura = 200;
			
			// Cria miniatura
			BufferedImage resizedImage = new BufferedImage(altura, largura, type);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(bufferedImage, 0, 0, largura, altura, null);
			g.dispose();
			
			// Escreve a imagem em tamanho menor
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			String extensao = arquivoFoto.getContentType().split("\\/")[1];
			ImageIO.write(resizedImage, extensao, baos);
			
			String miniImage = "data:" + arquivoFoto.getContentType() + ";base64," 
							 + DatatypeConverter.printBase64Binary(baos.toByteArray());
			
			// Processa imagem
			pessoa.setFotoIconBase64(miniImage);
			pessoa.setExtensao(extensao);
			
		}
		
		
		pessoa = daoGeneric.merge(pessoa);
		carregarPessoas();
		mostrarMsg("Cadastrado com sucesso!");
		
		return "";
	}
	
	public String novo() {
		pessoa = new Pessoa();
		
		return "";
	}
	
	public String limpar() {
		pessoa = new Pessoa();
		
		return "";
	}
	
	public String remove() {
		daoGeneric.deletePorId(pessoa);
		pessoa = new Pessoa();
		
		carregarPessoas();
		mostrarMsg("Removido com sucesso");
		
		return "";
	}

	@PostConstruct
	public void carregarPessoas() {
		pessoas = daoGeneric.getListEntityLimit10(Pessoa.class);
	}
	
	public void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);
		
	}
	
	public void pesquisaCep(AjaxBehaviorEvent event) {

		try {
			
			URL url = new URL("https://viacep.com.br/ws/" + pessoa.getCep() + "/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			String cep = "";
			StringBuilder jsonCep = new StringBuilder();
			
			while((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}
			
			Pessoa gsonAux = new Gson().fromJson(jsonCep.toString(), Pessoa.class);
			
			pessoa.setCep(cep);
			pessoa.setLogradouro(gsonAux.getLogradouro());
			pessoa.setComplemento(gsonAux.getComplemento());
			pessoa.setBairro(gsonAux.getBairro());
			pessoa.setLocalidade(gsonAux.getLocalidade());
			pessoa.setUf(gsonAux.getUf());
			pessoa.setUnidade(gsonAux.getUnidade());
			pessoa.setIbge(gsonAux.getIbge());
			pessoa.setGia(gsonAux.getGia());
			
		} catch (Exception e) {
			e.printStackTrace();
			mostrarMsg("Erro ao consultar o CEP");
		}
	
	}
	
	
	public String logar() {
		Pessoa pessoaUser = iDaoPessoa.consultaUsuario(pessoa.getLogin(), pessoa.getSenha());
		
		if (pessoaUser != null) { // Achou o usuario
			// Adicionar o usuario na sessao usuarioLogado
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("usuarioLogado", pessoaUser);
			
			return "primeirapagina.jsf";
		} else {
			FacesContext.getCurrentInstance().addMessage("msg", new FacesMessage("Usuário e/ou senha incorretos"));
		}
		
		return "index.jsf";
	}
	
	
	public String deslogar() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("usuarioLogado");
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
		httpServletRequest.getSession().invalidate();
		
		externalContext.getSessionMap().put("usuarioLogado", null);
		
		return "index.jsf";
	}
	
	
	public boolean permiteAcesso(String acesso) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		
		return pessoaUser.getPerfilUser().equals(acesso);
	}
	
	public List<SelectItem> getEstados() {
		estados = iDaoPessoa.listaEstados();
		return estados;
	}
	
	public void carregaCidades(AjaxBehaviorEvent event) {
		Estados estado = (Estados) ((SelectOneMenu) event.getSource()).getValue();
		
			if (estado != null) {
				pessoa.setEstado(estado);
				
				List<Cidades> cidades = jpaUtil.getEntityManager()
										.createQuery("from Cidades where estados.id = " + estado.getId())
										.getResultList();
				
				List<SelectItem> selectItems = new ArrayList<SelectItem>();
				
				for (Cidades cidade : cidades) {
					selectItems.add(new SelectItem(cidade, cidade.getNome()));
				}
			
				setCidades(selectItems);
			}
			
	}
	
	public String editar() {
		if (pessoa.getCidade() != null) {
			Estados estado = pessoa.getCidade().getEstados();
			pessoa.setEstado(estado);
			
			List<Cidades> cidades = jpaUtil.getEntityManager()
					.createQuery("from Cidades where estados.id = " + estado.getId())
					.getResultList();

			List<SelectItem> selectItems = new ArrayList<SelectItem>();
			
			for (Cidades cidade : cidades) {
			selectItems.add(new SelectItem(cidade, cidade.getNome()));
			}
			
			setCidades(selectItems);
		}
		return "";
	}
	
	// Metodo que converte inputStream para array de bytes
	public byte[] getByte(InputStream is) throws IOException {
		int len;
		int size = 1024;
		byte[] buf = null;
		
		if (is instanceof ByteArrayInputStream) {
			size = is.available();
			buf = new byte[size];
			len = is.read(buf, 0, size);
		} else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			buf = new byte[size];
			
			while ((len = is.read(buf, 0, size)) != -1) {
				bos.write(buf, 0, len);
			}
			
			buf = bos.toByteArray();
		}
		
		return buf;
	}
	
	public void download() throws IOException {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String fileDownloadId = params.get("fileDownloadId");
		
		Pessoa pessoa = daoGeneric.consultar(Pessoa.class, fileDownloadId);
	
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance()
									   .getExternalContext()
									   .getResponse();
		
		response.addHeader("Content-Disposition", "attachment; filename=download." + pessoa.getExtensao());
		response.setContentType("application/octet-stream");
		response.setContentLength(pessoa.getFotoIconBase64Original().length);
		response.getOutputStream().write(pessoa.getFotoIconBase64Original());
		response.getOutputStream().flush();
		
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	public void registraLog() {
		// E' executado antes do action
		System.out.println("Registra log");
	}
	
	public void mudancaNome(ValueChangeEvent evento) {
		System.out.println("Valor antigo: " + evento.getOldValue());
	}
	
	public Part getArquivoFoto() {
		return arquivoFoto;
	}
	public void setArquivoFoto(Part arquivoFoto) {
		this.arquivoFoto = arquivoFoto;
	}
	public List<SelectItem> getCidades() {
		return cidades;
	}
	public void setCidades(List<SelectItem> cidades) {
		this.cidades = cidades;
	}
	public Pessoa getPessoa() {
		return pessoa;
	}
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	public List<Pessoa> getPessoas() {
		return pessoas;
	}

	
}
