package br.com.repository;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Estados;
import br.com.entidades.Lancamento;
import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;

@Named
public class IDaoPessoaImpl implements IDaoPessoa, Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager entityManager;
	
	@Override
	public Pessoa consultaUsuario(String login, String senha) {
		Pessoa pessoa = null;
		

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		
		try {
			pessoa = (Pessoa) entityManager.createQuery("select p from Pessoa p where p.login = '" + login + "' and p.senha = '" + senha + "'").getSingleResult();
		
		} catch (Exception e) {
			// Caso nao encontre um usuario com o login e/ou a senha informado
		
		}
		
		entityTransaction.commit();
		
		return pessoa;
	}

	@Override
	public List<SelectItem> listaEstados() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		
		List<Estados> estados = entityManager.createQuery("from Estados").getResultList();
		
		for (Estados estado : estados) {
			selectItems.add(new SelectItem(estado, estado.getNome()));
		}
		
		return selectItems;
	}

	@Override
	public List<Pessoa> relatorioPessoa(String nome, Date dataIni, Date dataFim) {
		List<Pessoa> pessoasList = new ArrayList<Pessoa>();
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("select p from Pessoa p ");
		
		if (dataIni == null && dataFim == null && nome != null && !nome.isEmpty()) {
			
			sql.append(" where upper(p.nome) like '%").append(nome.trim().toUpperCase()).append("%'");
		
		} else if (nome == null || (nome != null && nome.isEmpty())
				   && dataIni != null && dataFim == null) {
			
			String dataIniString = new SimpleDateFormat("yyyy-MM-dd").format(dataIni);
			
			sql.append(" where p.dataNascimento >= '").append(dataIniString).append("'");
		
		} else if (nome == null || (nome != null && nome.isEmpty())
				   && dataIni == null && dataFim != null) {
			
			String dataFimString = new SimpleDateFormat("yyyy-MM-dd").format(dataFim);
			
			sql.append(" where p.dataNascimento <= '").append(dataFimString).append("'");
		
		} else if (nome == null || (nome != null && nome.isEmpty())
				   && dataIni != null && dataFim != null) {
			
			String dataIniString = new SimpleDateFormat("yyyy-MM-dd").format(dataIni);
			String dataFimString = new SimpleDateFormat("yyyy-MM-dd").format(dataFim);
			
			sql.append(" where p.dataNascimento >= '").append(dataIniString)
			   .append("' and p.dataNascimento <= '").append(dataFimString).append("'");
		} else if (nome != null  && !nome.isEmpty()
				   && dataIni != null && dataFim != null) {
			
			String dataIniString = new SimpleDateFormat("yyyy-MM-dd").format(dataIni);
			String dataFimString = new SimpleDateFormat("yyyy-MM-dd").format(dataFim);
			
			sql.append(" where p.dataNascimento >= '").append(dataIniString)
			   .append("' and p.dataNascimento <= '").append(dataFimString)
			   .append(" and upper(p.nome) like '%").append(nome.trim().toUpperCase()).append("%'");
		}
		
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		pessoasList = entityManager.createQuery(sql.toString()).getResultList();
		
		transaction.commit();
		
		return pessoasList;
	}

	
}
