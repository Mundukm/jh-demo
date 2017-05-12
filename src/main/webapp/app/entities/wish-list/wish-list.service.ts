import { Injectable } from '@angular/core';
import { Http, Response, URLSearchParams, BaseRequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { WishList } from './wish-list.model';
import { DateUtils } from 'ng-jhipster';

@Injectable()
export class WishListService {

    private resourceUrl = 'api/wish-lists';
    private resourceSearchUrl = 'api/_search/wish-lists';

    constructor(private http: Http, private dateUtils: DateUtils) { }

    create(wishList: WishList): Observable<WishList> {
        const copy = this.convert(wishList);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    update(wishList: WishList): Observable<WishList> {
        const copy = this.convert(wishList);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<WishList> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            const jsonResponse = res.json();
            jsonResponse.creationDate = this.dateUtils
                .convertLocalDateFromServer(jsonResponse.creationDate);
            return jsonResponse;
        });
    }

    query(req?: any): Observable<Response> {
        const options = this.createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map((res: Response) => this.convertResponse(res))
        ;
    }

    delete(id: number): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${id}`);
    }

    search(req?: any): Observable<Response> {
        const options = this.createRequestOption(req);
        return this.http.get(this.resourceSearchUrl, options)
            .map((res: any) => this.convertResponse(res))
        ;
    }

    private convertResponse(res: Response): Response {
        const jsonResponse = res.json();
        for (let i = 0; i < jsonResponse.length; i++) {
            jsonResponse[i].creationDate = this.dateUtils
                .convertLocalDateFromServer(jsonResponse[i].creationDate);
        }
        res.json().data = jsonResponse;
        return res;
    }

    private createRequestOption(req?: any): BaseRequestOptions {
        const options: BaseRequestOptions = new BaseRequestOptions();
        if (req) {
            const params: URLSearchParams = new URLSearchParams();
            params.set('page', req.page);
            params.set('size', req.size);
            if (req.sort) {
                params.paramsMap.set('sort', req.sort);
            }
            params.set('query', req.query);

            options.search = params;
        }
        return options;
    }

    private convert(wishList: WishList): WishList {
        const copy: WishList = Object.assign({}, wishList);
        copy.creationDate = this.dateUtils
            .convertLocalDateToServer(wishList.creationDate);
        return copy;
    }
}
