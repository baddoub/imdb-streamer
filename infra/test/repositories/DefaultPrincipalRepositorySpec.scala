package repositories

import domain.Actor
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
        val p1 = principal.copy(personId = person.id, titleId = title.id)
        val p2 = principal.copy(personId = person.id, titleId = title.id)
        db.principalRepository.save(p1).unsafeRunSync()
        db.principalRepository.save(p2).unsafeRunSync()
        val actual = db.principalRepository.principalsOfMovieName(title.primaryTitle).unsafeRunSync()
        actual shouldBe Seq(
          Actor(person.primaryName, person.primaryProfession, p1.job, p1.characters),
          Actor(person.primaryName, person.primaryProfession, p2.job, p2.characters),
        )
      }
    }
  }

}
