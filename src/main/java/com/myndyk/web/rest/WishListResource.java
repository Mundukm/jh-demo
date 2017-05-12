package com.myndyk.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.myndyk.domain.WishList;

import com.myndyk.repository.WishListRepository;
import com.myndyk.repository.search.WishListSearchRepository;
import com.myndyk.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing WishList.
 */
@RestController
@RequestMapping("/api")
public class WishListResource {

    private final Logger log = LoggerFactory.getLogger(WishListResource.class);

    private static final String ENTITY_NAME = "wishList";

    private final WishListRepository wishListRepository;

    private final WishListSearchRepository wishListSearchRepository;

    public WishListResource(WishListRepository wishListRepository, WishListSearchRepository wishListSearchRepository) {
        this.wishListRepository = wishListRepository;
        this.wishListSearchRepository = wishListSearchRepository;
    }

    /**
     * POST  /wish-lists : Create a new wishList.
     *
     * @param wishList the wishList to create
     * @return the ResponseEntity with status 201 (Created) and with body the new wishList, or with status 400 (Bad Request) if the wishList has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/wish-lists")
    @Timed
    public ResponseEntity<WishList> createWishList(@Valid @RequestBody WishList wishList) throws URISyntaxException {
        log.debug("REST request to save WishList : {}", wishList);
        if (wishList.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new wishList cannot already have an ID")).body(null);
        }
        WishList result = wishListRepository.save(wishList);
        wishListSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/wish-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /wish-lists : Updates an existing wishList.
     *
     * @param wishList the wishList to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated wishList,
     * or with status 400 (Bad Request) if the wishList is not valid,
     * or with status 500 (Internal Server Error) if the wishList couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/wish-lists")
    @Timed
    public ResponseEntity<WishList> updateWishList(@Valid @RequestBody WishList wishList) throws URISyntaxException {
        log.debug("REST request to update WishList : {}", wishList);
        if (wishList.getId() == null) {
            return createWishList(wishList);
        }
        WishList result = wishListRepository.save(wishList);
        wishListSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, wishList.getId().toString()))
            .body(result);
    }

    /**
     * GET  /wish-lists : get all the wishLists.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of wishLists in body
     */
    @GetMapping("/wish-lists")
    @Timed
    public List<WishList> getAllWishLists() {
        log.debug("REST request to get all WishLists");
        //only users wishes
        List<WishList> wishLists = wishListRepository.findByUserIsCurrentUser();
        return wishLists;
    }

    /**
     * GET  /wish-lists/:id : get the "id" wishList.
     *
     * @param id the id of the wishList to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the wishList, or with status 404 (Not Found)
     */
    @GetMapping("/wish-lists/{id}")
    @Timed
    public ResponseEntity<WishList> getWishList(@PathVariable Long id) {
        log.debug("REST request to get WishList : {}", id);
        WishList wishList = wishListRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(wishList));
    }

    /**
     * DELETE  /wish-lists/:id : delete the "id" wishList.
     *
     * @param id the id of the wishList to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/wish-lists/{id}")
    @Timed
    public ResponseEntity<Void> deleteWishList(@PathVariable Long id) {
        log.debug("REST request to delete WishList : {}", id);
        wishListRepository.delete(id);
        wishListSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/wish-lists?query=:query : search for the wishList corresponding
     * to the query.
     *
     * @param query the query of the wishList search
     * @return the result of the search
     */
    @GetMapping("/_search/wish-lists")
    @Timed
    public List<WishList> searchWishLists(@RequestParam String query) {
        log.debug("REST request to search WishLists for query {}", query);
        return StreamSupport
            .stream(wishListSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
