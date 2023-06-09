package br.com.cursojsf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.dao.DAOGeneric;
import br.com.entidades.Lancamento;
import br.com.repository.IDaoLancamento;

@ViewScoped
@Named(value = "relLancamento")
public class RelLancamento implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date dataIni;
	private Date dataFim;
	
	private String numNota;
	
	private List<Lancamento> lancamentos = new ArrayList<Lancamento>();

	@Inject
	private IDaoLancamento daoLancamento;
	
	@Inject
	private DAOGeneric<Lancamento> daoGeneric;
	
	
	public void buscarLancamento() {
		
		if(dataIni == null && dataFim == null && numNota == null) {
			lancamentos = daoGeneric.getListEntity(Lancamento.class);
		} else {
			lancamentos = daoLancamento.relatorioLancamento(numNota, dataIni, dataFim);
		}
		
	}
	
	
	public Date getDataIni() {
		return dataIni;
	}
	public void setDataIni(Date dataIni) {
		this.dataIni = dataIni;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	public String getNumNota() {
		return numNota;
	}
	public void setNumNota(String numNota) {
		this.numNota = numNota;
	}
	public List<Lancamento> getLancamentos() {
		return lancamentos;
	}
	public void setLancamentos(List<Lancamento> lancamentos) {
		this.lancamentos = lancamentos;
	}
}
