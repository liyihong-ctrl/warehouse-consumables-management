import { post } from '../../utils/http'

export const getConsumablesApi = p => post('/cStatistics/getConsumables', p)

export const getConsumableStatisticsApi = p => post('/cStatistics/getConsumableStatistics', p)
