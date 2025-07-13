// src/services/cepService.ts

export interface Endereco {
  cep: string
  logradouro: string
  complemento: string
  bairro: string
  localidade: string
  uf: string
  erro?: boolean
}

export async function buscarEnderecoPorCep(cep: string): Promise<Endereco | null> {
  const cepFormatado = cep.replace(/\D/g, '')

  if (cepFormatado.length !== 8) {
    return null
  }

  try {
    const response = await fetch(`https://viacep.com.br/ws/${cepFormatado}/json/`)
    const data: Endereco = await response.json()

    if (data.erro) return null

    return data
  } catch (error) {
    console.error('Erro ao buscar o CEP:', error)
    return null
  }
}
