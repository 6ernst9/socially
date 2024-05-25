import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';

export const request = async<T = any>(config: AxiosRequestConfig): Promise<AxiosResponse<T>> => {
    return axios(config);
}
