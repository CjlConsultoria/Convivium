package br.com.convivium.service;

import br.com.convivium.dto.response.CondominioInfoDTO;
import br.com.convivium.entity.CondominioInfo;
import br.com.convivium.entity.Empresa;
import br.com.convivium.exception.ApiException;
import br.com.convivium.repository.CondominioInfoRepository;
import br.com.convivium.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CondominioInfoService {

    private final CondominioInfoRepository condominioInfoRepository;
    private final EmpresaRepository empresaRepository;

    public CondominioInfoService(CondominioInfoRepository condominioInfoRepository,
                                 EmpresaRepository empresaRepository) {
        this.condominioInfoRepository = condominioInfoRepository;
        this.empresaRepository = empresaRepository;
    }

    public CondominioInfoDTO buscarPorEmpresaId(Long empresaId) {
        CondominioInfo info = condominioInfoRepository.findByCondominio_Id(empresaId)
                .orElse(null);
        if (info == null) {
            return null;
        }
        return toDTO(info);
    }

    public CondominioInfoDTO buscarPorCodigoPublico(String codigoPublico) {
        Empresa empresa = empresaRepository.findByCodigoPublico(codigoPublico);
        if (empresa == null) {
            return null;
        }
        return buscarPorEmpresaId(empresa.getId());
    }

    @Transactional
    public CondominioInfoDTO criarOuAtualizar(Long empresaId, CondominioInfoDTO dto) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ApiException.NotFoundException("Condomínio não encontrado."));

        CondominioInfo info = condominioInfoRepository.findByCondominio_Id(empresaId)
                .orElse(new CondominioInfo());

        if (info.getId() == null) {
            info.setCondominio(empresa);
        }

        mapDtoToEntity(dto, info);
        info = condominioInfoRepository.save(info);
        return toDTO(info);
    }

    private void mapDtoToEntity(CondominioInfoDTO dto, CondominioInfo info) {
        if (dto.getHorarioPiscina() != null) info.setHorarioPiscina(dto.getHorarioPiscina());
        if (dto.getHorarioAcademia() != null) info.setHorarioAcademia(dto.getHorarioAcademia());
        if (dto.getHorarioChurrasco() != null) info.setHorarioChurrasco(dto.getHorarioChurrasco());
        if (dto.getHorarioSalaoFestas() != null) info.setHorarioSalaoFestas(dto.getHorarioSalaoFestas());
        if (dto.getHorarioQuadra() != null) info.setHorarioQuadra(dto.getHorarioQuadra());
        if (dto.getHorarioElevador() != null) info.setHorarioElevador(dto.getHorarioElevador());
        if (dto.getHorarioPlayground() != null) info.setHorarioPlayground(dto.getHorarioPlayground());
        if (dto.getTelefonePortaria() != null) info.setTelefonePortaria(dto.getTelefonePortaria());
        if (dto.getContatoSindico() != null) info.setContatoSindico(dto.getContatoSindico());
        if (dto.getContatoAdministradora() != null) info.setContatoAdministradora(dto.getContatoAdministradora());
        if (dto.getHorarioBarulho() != null) info.setHorarioBarulho(dto.getHorarioBarulho());
        if (dto.getRegrasGerais() != null) info.setRegrasGerais(dto.getRegrasGerais());
        if (dto.getRegrasGaragem() != null) info.setRegrasGaragem(dto.getRegrasGaragem());
        if (dto.getRegrasPiscina() != null) info.setRegrasPiscina(dto.getRegrasPiscina());
        if (dto.getRegrasChurrasqueira() != null) info.setRegrasChurrasqueira(dto.getRegrasChurrasqueira());
        if (dto.getRegrasSalaoFestas() != null) info.setRegrasSalaoFestas(dto.getRegrasSalaoFestas());
        if (dto.getRegrasQuadra() != null) info.setRegrasQuadra(dto.getRegrasQuadra());
        if (dto.getLimpeza() != null) info.setLimpeza(dto.getLimpeza());
        if (dto.getManutencao() != null) info.setManutencao(dto.getManutencao());
        if (dto.getIluminacao() != null) info.setIluminacao(dto.getIluminacao());
        if (dto.getAgua() != null) info.setAgua(dto.getAgua());
        if (dto.getColetaLixo() != null) info.setColetaLixo(dto.getColetaLixo());
        if (dto.getBoletoLink() != null) info.setBoletoLink(dto.getBoletoLink());
        if (dto.getFormaPagamento() != null) info.setFormaPagamento(dto.getFormaPagamento());
        if (dto.getTaxaCondominio() != null) info.setTaxaCondominio(dto.getTaxaCondominio());
        if (dto.getReservaLink() != null) info.setReservaLink(dto.getReservaLink());
        if (dto.getPoliticaReservas() != null) info.setPoliticaReservas(dto.getPoliticaReservas());
        if (dto.getEstacionamentoInfo() != null) info.setEstacionamentoInfo(dto.getEstacionamentoInfo());
        if (dto.getVisitantesInfo() != null) info.setVisitantesInfo(dto.getVisitantesInfo());
        if (dto.getAnimaisInfo() != null) info.setAnimaisInfo(dto.getAnimaisInfo());
    }

    private CondominioInfoDTO toDTO(CondominioInfo info) {
        CondominioInfoDTO dto = new CondominioInfoDTO();
        dto.setId(info.getId());
        if (info.getCondominio() != null) {
            dto.setEmpresaId(info.getCondominio().getId());
            dto.setNomeCondominio(info.getCondominio().getName());
        }
        dto.setHorarioPiscina(info.getHorarioPiscina());
        dto.setHorarioAcademia(info.getHorarioAcademia());
        dto.setHorarioChurrasco(info.getHorarioChurrasco());
        dto.setHorarioSalaoFestas(info.getHorarioSalaoFestas());
        dto.setHorarioQuadra(info.getHorarioQuadra());
        dto.setHorarioElevador(info.getHorarioElevador());
        dto.setHorarioPlayground(info.getHorarioPlayground());
        dto.setTelefonePortaria(info.getTelefonePortaria());
        dto.setContatoSindico(info.getContatoSindico());
        dto.setContatoAdministradora(info.getContatoAdministradora());
        dto.setHorarioBarulho(info.getHorarioBarulho());
        dto.setRegrasGerais(info.getRegrasGerais());
        dto.setRegrasGaragem(info.getRegrasGaragem());
        dto.setRegrasPiscina(info.getRegrasPiscina());
        dto.setRegrasChurrasqueira(info.getRegrasChurrasqueira());
        dto.setRegrasSalaoFestas(info.getRegrasSalaoFestas());
        dto.setRegrasQuadra(info.getRegrasQuadra());
        dto.setLimpeza(info.getLimpeza());
        dto.setManutencao(info.getManutencao());
        dto.setIluminacao(info.getIluminacao());
        dto.setAgua(info.getAgua());
        dto.setColetaLixo(info.getColetaLixo());
        dto.setBoletoLink(info.getBoletoLink());
        dto.setFormaPagamento(info.getFormaPagamento());
        dto.setTaxaCondominio(info.getTaxaCondominio());
        dto.setReservaLink(info.getReservaLink());
        dto.setPoliticaReservas(info.getPoliticaReservas());
        dto.setEstacionamentoInfo(info.getEstacionamentoInfo());
        dto.setVisitantesInfo(info.getVisitantesInfo());
        dto.setAnimaisInfo(info.getAnimaisInfo());
        dto.setDataCriacao(info.getDataCriacao());
        dto.setDataAtualizacao(info.getDataAtualizacao());
        return dto;
    }
}
