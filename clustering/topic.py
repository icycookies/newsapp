import jieba
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.decomposition import LatentDirichletAllocation

def print_top_words(model, feature_names, n_top_words):
        #打印每个主题下权重较高的term
    for topic_idx, topic in enumerate(model.components_):
        print("Topic #%d:" % topic_idx)
        print(" ".join([feature_names[i]
                        for i in topic.argsort()[:-n_top_words - 1:-1]]))
    #print (model.components_)

docs = []
i = 0
with open("titles") as file:
    for title in file:
        doc_cut = jieba.cut(title)
        result = ' '.join(doc_cut)
        i += 1
        if i > 2000:
            break
        docs.append(result)

for i in range(5):
    corpus = []
    j = 0
    with open("output") as file:
        for num in file:
            if int(num) == i:
                corpus.append(docs[j])
            j += 1
            if j >= 2000:
                break
    #print(corpus)
    cntVector = CountVectorizer()
    cntTf = cntVector.fit_transform(corpus)
    print("orz")

    lda = LatentDirichletAllocation(n_components = 2, learning_offset = 50)
    decres = lda.fit_transform(cntTf)
    #print(lda.components_)

    n_top_words=20
    tf_feature_names = cntVector.get_feature_names()
    print_top_words(lda, tf_feature_names, n_top_words)
