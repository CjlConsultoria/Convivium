import api from './api'

export const fetchLicencas = async () => {
  const res = await api.get('/licencas/search')
  return res.data
}

export const criarOuAtualizarLicenca = async (licencaData: any) => {
  const res = await api.post('/licencas', licencaData)
  return res.data
}

export const deleteLicencaById = async (id: number) => {
  const res = await api.delete(`/licencas/${id}`)
  return res.data
}
