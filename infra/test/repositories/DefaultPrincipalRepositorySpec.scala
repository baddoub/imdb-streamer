package repositories

import domain.Principal
import domain.Title.TitleType.Movie

class DefaultPrincipalRepositorySpec extends BaseRepoSpec {

  describe("DefaultPrincipalRepos") {
    describe("save") {
      it("should save") {
        db.personRepository.save(person).unsafeRunSync()
        db.titleRepository.save(title).unsafeRunSync()
        val newPrincipal = principal.copy(personId = person.id, titleId = title.id)
        db.principalRepository.save(newPrincipal).unsafeRunSync()
        db.principalRepository.selectAll().unsafeRunSync() shouldBe Seq(newPrincipal)
      }
    }
    describe("principalsOfMovieName") {
      it("should select principals of a movie from its name") {
        db.personRepository.save(person).unsafeRunSync()
        db.titleRepository.save(title.copy(titleType = Movie)).unsafeRunSync()
        val expected = principal.copy(personId = person.id, titleId = title.id)
        db.principalRepository.save(expected).unsafeRunSync()
        val actual: Seq[Principal] = db.principalRepository.principalsOfMovieName(title.primaryTitle).unsafeRunSync()
        actual shouldBe Seq(expected)
      }
    }
  }

}
