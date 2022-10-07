package me.snsservice.article.service;

import lombok.RequiredArgsConstructor;
import me.snsservice.article.domain.Article;
import me.snsservice.article.dto.ArticleResponse;
import me.snsservice.article.dto.CreateArticleRequest;
import me.snsservice.article.dto.UpdateArticleRequest;
import me.snsservice.article.repository.ArticleRepository;
import me.snsservice.common.error.exception.BusinessException;
import me.snsservice.member.domain.Member;
import me.snsservice.tag.domain.Tag;
import me.snsservice.tag.domain.Tags;
import me.snsservice.tag.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static me.snsservice.common.error.ErrorCode.NOT_FOUND_ARTICLE;
import static me.snsservice.common.error.ErrorCode.UNAUTHORIZED_ARTICLE_MEMBER;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;

    @Transactional
    public Long createArticle(CreateArticleRequest createArticleRequest, Member member) {
        Article article = articleRepository.save(createArticleRequest.toEntity(member));
        Tags tags = createTags(Tags.from(createArticleRequest.getTags()));
        article.addTags(tags);
        return article.getId();
    }

    private Tags createTags(Tags tags) {
        return new Tags(tags.getTagNames().stream()
                .map(this::createTag)
                .collect(Collectors.toList()));
    }

    private Tag createTag(String name) {
        return tagRepository.findByName(name)
                .orElseGet(() -> tagRepository.save(new Tag(name)));
    }

    @Transactional
    public ArticleResponse findById(Long articleId) {
        Article article = getArticle(articleId);
        article.addViewCount();
        return ArticleResponse.of(article);
    }

    //Todo paging 처리
    @Transactional(readOnly = true)
    public List<ArticleResponse> findAll() {
        return articleRepository.findAll().stream()
                .map(ArticleResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateArticle(UpdateArticleRequest request, Long articleId, Long loginId) {
        Article article = getArticle(articleId);
        validateArticleMEmberIdAndMemberId(article, loginId);
        article.updateArticle(request.getTitle(), request.getContent());
    }

    @Transactional
    public void deleteArticle(Long articleId, Long loginId) {
        Article article = getArticle(articleId);
        validateArticleMEmberIdAndMemberId(article, loginId);
        article.deleteArticle();
    }

    private Article getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ARTICLE));
    }

    private void validateArticleMEmberIdAndMemberId(Article article, Long loginId) {
        if (!article.getMember().getId().equals(loginId)) {
            throw new BusinessException(UNAUTHORIZED_ARTICLE_MEMBER);
        }
    }
}
